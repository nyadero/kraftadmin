package com.bowerzlabs.crud;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.EntityMetaModel;
import com.bowerzlabs.InputGenerator;
import com.bowerzlabs.constants.*;
import com.bowerzlabs.data.DataExporter;
import com.bowerzlabs.data.DataExporterRegistry;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.dtos.FieldValue;
import com.bowerzlabs.dtos.PageResponse;
import com.bowerzlabs.dtos.Subject;
import com.bowerzlabs.events.UIEvent;
import com.bowerzlabs.events.UserActionEvent;
import com.bowerzlabs.files.LocalMultipartFileStorage;
import com.bowerzlabs.files.MultipartFileStorage;
import com.bowerzlabs.files.StorageProperties;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import com.bowerzlabs.service.CrudService;
import com.bowerzlabs.service.FileStorageService;
import com.bowerzlabs.utils.DisplayUtils;
import com.bowerzlabs.utils.KraftUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/crud")
//@AdminController
public class CrudController {
    private static final Logger log = LoggerFactory.getLogger(CrudController.class);
    private final EntitiesScanner entitiesScanner;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CrudService crudService;
    private final DataExporterRegistry dataExporterRegistry;
    private FileStorageService fileStorageService;
    private MultipartFileStorage multipartFileStorage;
    @Autowired
    private StorageProperties storageProperties;

    public CrudController(EntitiesScanner entitiesScanner, ApplicationEventPublisher applicationEventPublisher, CrudService crudService, DataExporterRegistry dataExporterRegistry) {
        this.entitiesScanner = entitiesScanner;
        this.applicationEventPublisher = applicationEventPublisher;
        this.crudService = crudService;
        this.dataExporterRegistry = dataExporterRegistry;
    }

    // render create item page
    @GetMapping("/{entityName}/create")
    public String renderCreateForm(
            @PathVariable("entityName") String entityName,
            @RequestParam Map<String, String> formData,
            Model model
    ){
        try {
            EntityMetaModel clazz = entitiesScanner.getEntityByName(entityName);
            Map<String, String> validationErrors = (Map<String, String>) model.getAttribute("validationErrors");
            DbObjectSchema dbObjectSchema = new DbObjectSchema(clazz,null);
            List<FormField> formFields = InputGenerator.generateFormInput(clazz, dbObjectSchema, "/admin/crud/" + entityName + "/create", false, false, validationErrors);
            log.info("formFields {}", formFields);
            model.addAttribute("actionUrl", "/admin/crud/" + entityName + "/create");
            model.addAttribute("fields", formFields);
            model.addAttribute("entityName", entityName);
            assert validationErrors != null;
//            model.addAttribute("validationErrors", !validationErrors.isEmpty() ? validationErrors : new HashMap<>());
            model.addAttribute("validationErrors", validationErrors);
        } catch (Exception e) {
            model.addAttribute("error", "Error rendering form " + e.getMessage());
        }
        return "kraft-crud/form";
    }

    // add item to database
    @PostMapping("/{entityName}/create")
    public String saveItem(
            @PathVariable("entityName") String entityName,
            @RequestParam Map<String, String> formValues,
            @RequestParam(required = false) Map<String, MultipartFile> fileInputs,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {
        try {
            EntityMetaModel entityInstance = entitiesScanner.getEntityByName(entityName);

            DbObjectSchema dbObjectSchema = new DbObjectSchema(entityInstance, null);
            DbObjectSchema savedEntity = null;


            System.out.println("Received form values: " + formValues);
            System.out.println("Received files: " + fileInputs);

            //  Validate input
            Map<String, String> validationErrors = dbObjectSchema.validateFormValues(formValues);
//            log.info("add data validation errors {}", validationErrors);
            if (!validationErrors.isEmpty()) {
                redirectAttributes.addFlashAttribute("validationErrors", validationErrors);
                redirectAttributes.addFlashAttribute("formValues", formValues);
                return "redirect:/admin/crud/" + entityName + "/create";
            }

            //  Upload files
            if (fileInputs != null && !fileInputs.isEmpty()) {
                for (Map.Entry<String, MultipartFile> entry : fileInputs.entrySet()) {
                    String field = entry.getKey();
                    MultipartFile file = entry.getValue();
                    String uploadedFile = null;
                    if (file != null && !file.isEmpty()) {
                        log.info("storage provider {} and directory {}", storageProperties.getProvider(), storageProperties.getUploadDir());
                        switch (storageProperties.getProvider()) {
                            case Local:
                                multipartFileStorage = new LocalMultipartFileStorage(request, storageProperties);
                                uploadedFile = multipartFileStorage.uploadSingle(file);
                                break;
                            case Cloudinary:
                                break;
                            case AWS_S3:
                                break;
                            default:
                                log.info("Unknown provider");
                        }
                        formValues.put(field, uploadedFile);
                    }
                    formValues.put(field, uploadedFile);
                }
            }
            Object entity = crudService.save(entityName, formValues, null);
            savedEntity = new DbObjectSchema(entityInstance, entity);
            applicationEventPublisher.publishEvent(new UIEvent(this, savedEntity.getClass().getSimpleName() + " saved successfully", Status.Success));
            Subject subject = new Subject(dbObjectSchema.getEntityName(), List.of(dbObjectSchema.getIdValue().toString()));
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Create,
                    (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));

        } catch (Exception e) {
            log.info("creation error {}", e.toString());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save entity: " + e.getMessage());
            applicationEventPublisher.publishEvent(new UIEvent(this, "Failed to save entity", Status.Error));
            return "redirect:/admin/crud/" + entityName;
        }

        return "redirect:/admin/crud/" + entityName;
    }

    // get entity by ID dynamically
    @GetMapping("/{entityName}/{id}")
    public String getItem(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            Model model
    ) {
        List<Map<String, FieldValue>> displayMap = new ArrayList<>();
        try {
            Optional<DbObjectSchema> item =  crudService.findById(entityName, id);
            assert item.isPresent();
            Map<String, Object> rawFields = item.get().getAllFieldsWithData();
            Map<String, FieldValue> displayItem = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : rawFields.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                FieldValue displayField = DisplayUtils.resolveFieldValue(value);
                displayItem.put(fieldName, displayField);
            }
            displayMap.add(displayItem);
            model.addAttribute("fieldNames", item.get().getAllFields().stream().map(Field::getName).toList());
            model.addAttribute("item", displayMap);
            Subject subject = new Subject(item.get().getEntityName(), List.of(item.get().getIdValue().toString()));
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Read, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching entity " + e.getMessage());
        }
        return "kraft-crud/details";
    }


    // render all items, use pagination
    @GetMapping("/{entityName}")
    public String fetchAll(
            @PathVariable("entityName") String entityName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Map<String, String> allParams1,
            Model model
    ) {
//        EntityMetaModel entityInstance = entitiesScanner.getEntityByName(entityName);
        EntityMetaModel entityInstance = entitiesScanner.getEntityByName(entityName);
        log.info("entityInstance {}", entityInstance);
        try {
            DbObjectSchema schema = new DbObjectSchema(entityInstance,null);
//            List<FormField> formFields = InputGenerator.generateFormInput(entityInstance, schema, "/admin/crud/" + entityName + "/create", false, true, new HashMap<>());
            List<Field> filterFields = schema.getFilterableFields();
            List<Field> searchFields = schema.getSearchableFields();
            List<Map<String, Object>> processedItems = new ArrayList<>();
            List<Map<String, FieldValue>> displayMap = new ArrayList<>();
            List<PerformableAction> performableActions = new ArrayList<>();
            List<String> idValues = new ArrayList<>();

            Map<String, String> filters = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams1.entrySet()) {
                if (entry.getKey().startsWith("filter.")) {
                    String fieldKey = entry.getKey().substring("filter.".length());
                    filters.put(fieldKey, entry.getValue());
                }
            }
            Page<DbObjectSchema> pagedResult = crudService.findAll(entityName, page, size, filters);
            List<String> sortableFieldNames = schema.getSortableFields().stream().map(field -> KraftUtils.formatLabel(field.getName())).toList();
            List<String> filterFieldNames = schema.getFilterableFields().stream().map(Field::getName).toList();
            List<String> filterableFieldNames = filterFields.stream()
                    .map(Field::getName) // This gets just "gender", "action", etc.
                    .collect(Collectors.toList());

            model.addAttribute("filterableFieldNames", filterableFieldNames);

            String primaryKey = "";
            DbObjectSchema firstItem = null;
            String sortBy = filters.getOrDefault("sortBy", "id");
            String sortOrder = filters.getOrDefault("sortOrder", "asc");
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortOrder", sortOrder);

            Map<String, String> displayToFieldMap = new HashMap<>();
            for (Field field : schema.getSortableFields()) {
                String rawField = field.getName();
                displayToFieldMap.put(KraftUtils.formatLabel(rawField), rawField);
            }
            model.addAttribute("displayToFieldMap", displayToFieldMap);
            List<String> displayFieldNames = displayToFieldMap.keySet().stream().toList();
            model.addAttribute("fieldNames", displayFieldNames);
            model.addAttribute("formats", DataFormat.values());

            if (!pagedResult.getContent().isEmpty()) {
                firstItem = pagedResult.getContent().get(0);
                List<String> fieldNames = new ArrayList<>(firstItem.getFieldsWithData().keySet());
                primaryKey = firstItem.getPrimaryKey();
                performableActions = firstItem.getPerformableActions();
                for (DbObjectSchema item : pagedResult.getContent()) {
                    processedItems.add(item.getFieldsWithData());
                    Map<String, Object> rawFields = item.getFieldsWithData();
                    Map<String, FieldValue> displayItem = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> entry : rawFields.entrySet()) {
                        String fieldName = entry.getKey();
                        Object value = entry.getValue();
                        FieldValue displayField = DisplayUtils.resolveFieldValue(value);
                        displayItem.put(fieldName, displayField);

                    }
                    idValues.add(item.getIdValue().toString());
                    displayMap.add(displayItem);
                }

                model.addAttribute("items", processedItems);  // Processed data
                model.addAttribute("displayItems", displayMap);
                model.addAttribute("fieldNames", fieldNames);
                model.addAttribute("actions", performableActions);
            }
            // Dynamically add enum values for each enum field
            Map<String, List<String>> enumFields = new HashMap<>();
            for (Field field : filterFields) {
                if (field.getType().isEnum()) {
                    // the enum values are accessible through reflection
                    Class<?> enumType = field.getType();
                    List<String> enumValues = Arrays.stream(enumType.getEnumConstants())
                            .map(enumConstant -> ((Enum<?>) enumConstant).name())
                            .collect(Collectors.toList());
                    enumFields.put(field.getName(), enumValues);
                }
            }

            Map<String, String> allParams = new HashMap<>(filters);
            allParams.put("page", String.valueOf(page));
//            allParams.put("size", String.valueOf(size));

            // Build a query string for pagination/sorting links
            String queryString = allParams.entrySet().stream()
                    .filter(e -> !e.getKey().equals("page")) // omit `page`, it will be replaced in pagination links
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            model.addAttribute("queryString", "&" + queryString);
            model.addAttribute("currentParams", allParams); // for hidden inputs
            model.addAttribute("enumFields", enumFields);
            model.addAttribute("entityName", entityName);
            model.addAttribute("primaryKey", KraftUtils.formatLabel(primaryKey));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", pagedResult.getTotalPages());
            model.addAttribute("totalElements", pagedResult.getTotalElements());
            model.addAttribute("pagination", new PageResponse(pagedResult.getContent(), pagedResult.getNumber(), pagedResult.getTotalElements(), pagedResult.getTotalPages()));
            model.addAttribute("searchableFieldNames", searchFields);
            model.addAttribute("sortableFieldNames", sortableFieldNames);
            model.addAttribute("filterableFieldNames", filterFieldNames);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortOrder", sortOrder);
//            model.addAttribute("fields", formFields);
            Subject subject = new Subject(schema.getEntityName(), idValues);
            log.info("filters {}, sortablefields {}, displayItems {}", filters, sortableFieldNames, displayMap);
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Read, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
        } catch (Exception e) {
            applicationEventPublisher.publishEvent(new UIEvent(this, "ERROR FETCHING DATA", Status.Error));
        }
        return "kraft-crud/list";
    }

    // render update item page
    @GetMapping("/{entityName}/edit/{id}")
    public String editForm(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            Model model
    ) {
        try {
            EntityMetaModel clazz = entitiesScanner.getEntityByName(entityName);
            Optional<DbObjectSchema> item = crudService.findById(entityName, id);
            Map<String, String> validationErrors = (Map<String, String>) model.getAttribute("validationErrors");
            if (item.isPresent()) {
                List<FormField> formFields = InputGenerator.generateFormInput(clazz, item.get(), "/admin/crud/" + entityName + "/edit", true, false, validationErrors);
                model.addAttribute("fields", formFields);
                model.addAttribute("entityName", entityName);
            }
        } catch (Exception e) {
            applicationEventPublisher.publishEvent(new UIEvent(this, e.getMessage(), Status.Error));
            return "redirect:/admin/crud/" + entityName;
        }

        return "kraft-crud/form";
    }

    // update item in the db
    @PostMapping("/{entityName}/edit/{id}")
    public String update(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            @RequestParam Map<String, String> formValues,
            RedirectAttributes redirectAttributes
    ) {
        EntityMetaModel entityInstance = entitiesScanner.getEntityByName(entityName);
        try {
            Optional<DbObjectSchema> item = crudService.findById(entityName, id);
            if (item.isPresent()) {
                DbObjectSchema dbObjectSchema = new DbObjectSchema(entityInstance, item.get().getEntity());
                Map<String, String> validationErrors = dbObjectSchema.validateFormValues(formValues);

                if (!validationErrors.isEmpty()) {
                    redirectAttributes.addFlashAttribute("validationErrors", validationErrors);
                    redirectAttributes.addFlashAttribute("formValues", formValues);
                    return "redirect:/admin/crud/" + entityName + "/edit";
                }
                // Save the updated entity
                Object savedEntity = crudService.save(entityName, formValues, item.get().getEntity());
                applicationEventPublisher.publishEvent(new UIEvent(this, entityName + " updated successfully", Status.Success));
                Subject subject = new Subject(item.get().getEntityName(), List.of(item.get().getIdValue().toString()));
                applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Update, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
            }
        } catch (Exception e) {
            applicationEventPublisher.publishEvent(new UIEvent(this, "Error updating " + entityName, Status.Error));
        }

        return "redirect:/admin/crud/" + entityName;
    }

    // delete item from db
    @GetMapping("/{entityName}/delete/{id}")
    public String deleteItem(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id
    ) {
        try {
            Optional<DbObjectSchema> item = crudService.findById(entityName, id);
            if (item.isPresent()){
                crudService.deleteById(entityName, id);
                Subject subject = new Subject(item.get().getEntityName(), List.of(item.get().getIdValue().toString()));
                applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Delete, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
                applicationEventPublisher.publishEvent(new UIEvent(this, entityName + " deleted successfully", Status.Error));
            }
        } catch (Exception e) {
            applicationEventPublisher.publishEvent(new UIEvent(this, "error deleting "+entityName, Status.Error));
        }
        return "redirect:/admin/crud/" + entityName;
    }

    // bulk operations
    @GetMapping("/{entityName}/bulk-action")
    public Object bulkOperations(
            @PathVariable("entityName") String entityName,
            @RequestParam(name = "action") String bulkAction,
            @RequestParam(name= "selectedIds") List<String> selectedIds,
            @RequestParam(name = "format", defaultValue = "JSON") String format,
            RedirectAttributes redirectAttributes,
            Model model
    ) throws Exception {
        log.info("action {} ids {}", bulkAction, selectedIds);
        switch (BulkAction.valueOf(bulkAction)){
            case Delete -> {
                crudService.bulkDelete(entityName, selectedIds);
                redirectAttributes.addFlashAttribute("success", "Items deleted successfully.");
//                return "redirect:/admin/crud/" + entityName;
            }
            case Export -> {
                crudService.exportData(entityName, selectedIds, format);
                redirectAttributes.addFlashAttribute("success", "Items exported successfully.");
            }
            case Print -> {
                // Todo
            }
            case Duplicate -> {
                // Todo -- create new data similar to the selected ones
            }
            default -> {
                redirectAttributes.addFlashAttribute("error", "Unknown bulk action.");
            }
        }
        return "redirect:/admin/crud/" + entityName;
    }

    /**
     * export entity data in defined format
     */
    @GetMapping("/{entityName}/export")
    public Object exportEntityData(
            @RequestParam(defaultValue = "json") String format,
            @PathVariable("entityName") String entityName
    ) {
        byte[] content = null;
        HttpHeaders headers = null;
        try {
            EntityMetaModel entityMetaModel = entitiesScanner.getEntityByName(entityName);
            List<Map<String, Object>> data = crudService.getAll(entityName).stream().map(DbObjectSchema::getAllFieldsWithData).toList();
            DataExporter exporter = dataExporterRegistry.getExporter(format);
            content = exporter.export(data);
            headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(exporter.getContentType()));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(entityName + exporter.getFileExtension()).build());
        } catch (Exception e) {
            log.info("exception {}", e.toString());
            applicationEventPublisher.publishEvent(new UIEvent(this, e.getMessage(), Status.Error));
            return "redirect:/admin/crud/" + entityName;
        }
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    /**
     * download file
      */
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        Resource file = null;
        try {
            file = fileStorageService.download(filename);
        } catch (Exception e) {
            applicationEventPublisher.publishEvent(new UIEvent(this, e.getMessage(), Status.Error));
            log.info("error {}", e.toString());
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(file);
    }

    
}
