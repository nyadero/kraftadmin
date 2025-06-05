package com.bowerzlabs.crud;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.InputGenerator;
import com.bowerzlabs.constants.FieldType;
import com.bowerzlabs.constants.Status;
import com.bowerzlabs.constants.UserActionType;
import com.bowerzlabs.data.DataExporter;
import com.bowerzlabs.data.DataExporterRegistry;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.dtos.FieldValue;
import com.bowerzlabs.dtos.PageResponse;
import com.bowerzlabs.dtos.TableRowDTO;
import com.bowerzlabs.events.UIEvent;
import com.bowerzlabs.events.UserActionEvent;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import com.bowerzlabs.service.CrudService;
import com.bowerzlabs.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/admin")
public class CrudController {
    private static final Logger log = LoggerFactory.getLogger(CrudController.class);
    private final EntitiesScanner entitiesScanner;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CrudService crudService;
    private final DataExporterRegistry dataExporterRegistry;
    private final FileStorageService fileStorageService;
//    private final LocalStorageServiceProvider localStorageServiceProvider;

    public CrudController(EntitiesScanner entitiesScanner, ApplicationEventPublisher applicationEventPublisher, CrudService crudService, DataExporterRegistry dataExporterRegistry) {
        this.entitiesScanner = entitiesScanner;
        this.applicationEventPublisher = applicationEventPublisher;
        this.crudService = crudService;
        this.dataExporterRegistry = dataExporterRegistry;
        this.fileStorageService = new FileStorageService();
    }

    // render create item page
    @GetMapping("/{entityName}/create")
    public String renderCreateForm(
            @PathVariable("entityName") String entityName,
            @RequestParam Map<String, String> formData, // Capture submitted data
            Model model
    ){
        try {
            Class<?> entityInstance = entitiesScanner.getEntityByName(entityName);
            System.out.println("entityInstance " + entityInstance.getSimpleName());
            Map<String, String> validationErrors = (Map<String, String>) model.getAttribute("validationErrors");
            log.info("Validation errors in renderCreateForm {} and formData {}", validationErrors, model.getAttribute("formValues"));
            DbObjectSchema dbObjectSchema = new DbObjectSchema(entityInstance,null);
            List<FormField> formFields = InputGenerator.generateFormInput(entityInstance, dbObjectSchema, "/admin/" + entityName + "/create", false, false,validationErrors);
            model.addAttribute("actionUrl", "/admin/" + entityName + "/create");
            model.addAttribute("fields", formFields);
            model.addAttribute("entityName", entityName);
            assert validationErrors != null;
//            model.addAttribute("validationErrors", !validationErrors.isEmpty() ? validationErrors : new HashMap<>());
            model.addAttribute("validationErrors", validationErrors);
        } catch (Exception e) {
//            throw new RuntimeException("Could not instantiate entity", e);
            model.addAttribute("error", "Error rendering form " + e.getMessage());
        }
        return "crud/form";
    }
    
    // add item to database
    @PostMapping("/{entityName}/create")
    public String saveItem(
            @PathVariable("entityName") String entityName,
            @RequestParam Map<String, String> formValues,
            @RequestParam(required = false) Map<String, MultipartFile> fileInputs,
            RedirectAttributes redirectAttributes
    ) {
        Object savedEntity = null;
        try {
            Class<?> entityInstance = entitiesScanner.getEntityByName(entityName);
            DbObjectSchema dbObjectSchema = new DbObjectSchema(entityInstance, null);

            System.out.println("📌 Received form values: " + formValues);
            System.out.println("📦 Received files: " + fileInputs);

            // 1. Validate input
            Map<String, String> validationErrors = dbObjectSchema.validateFormValues(formValues);
            if (!validationErrors.isEmpty()) {
                redirectAttributes.addFlashAttribute("validationErrors", validationErrors);
                redirectAttributes.addFlashAttribute("formValues", formValues);
                return "redirect:/admin/" + entityName + "/create";
            }

            // 2. Upload files
            if (fileInputs != null && !fileInputs.isEmpty()) {
                for (Map.Entry<String, MultipartFile> entry : fileInputs.entrySet()) {
                    String field = entry.getKey();
                    MultipartFile file = entry.getValue();

                    if (file != null && !file.isEmpty()) {
                        String uploadedFileName = fileStorageService.upload(file);
                        formValues.put(field, uploadedFileName); // Add it as if it was submitted in the form
                        System.out.println("📁 File uploaded for field " + field + ": " + uploadedFileName);
                    }
                }
            }

            // 3. Save entity with formValues (now includes file names)
            savedEntity = crudService.save(entityName, formValues, null);
            applicationEventPublisher.publishEvent(new UIEvent(this, savedEntity.getClass().getSimpleName() + " saved successfully", Status.Success));
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Create,
                    (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), entityName));

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save entity: " + e.getMessage());
            applicationEventPublisher.publishEvent(new UIEvent(this, "Failed to save entity", Status.Error));
        }

        return "redirect:/admin/" + entityName;
    }

    // get entity by ID dynamically
    @GetMapping("/{entityName}/{id}")
    public String getItem(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            Model model
    ) {
        try {
            Optional<DbObjectSchema> item =  crudService.findById(entityName, id);
            assert item.isPresent();
            model.addAttribute("item", item.get().getAllFieldsWithData());
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Read, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), entityName));
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching entity " + e.getMessage());
        }
        return "crud/details";
    }

    // render all items, use pagination
    @GetMapping("/{entityName}")
    public String fetchAll(
            @PathVariable("entityName") String entityName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Map<String, String> filters,
            @RequestParam(name = "sort", required = false) List<String> sortParams,
            Model model
    ) {
        Class<?> entityInstance = entitiesScanner.getEntityByName(entityName);
        try {
            DbObjectSchema schema = new DbObjectSchema(entitiesScanner.getEntityByName(entityName),null);
            List<FormField> formFields = InputGenerator.generateFormInput(entityInstance, schema, "/admin/" + entityName + "/create", false, true, new HashMap<>());
            List<Field> sortFields = schema.getSortableFields();
            List<Field> filterFields = schema.getFilterableFields();
            List<Field> searchFields = schema.getSearchableFields();
            List<Map<String, Object>> processedItems = new ArrayList<>();
            List<Map<String, FieldValue>> displayMap = new ArrayList<>();
            List<TableRowDTO> rows = new ArrayList<>();
            Map<String, String> cleanedFilters = new HashMap<>();
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                if (entry.getKey().startsWith("filter.")) {
                    String fieldKey = entry.getKey().substring("filter.".length());
                    cleanedFilters.put(fieldKey, entry.getValue());
                }
            }
            filters = cleanedFilters;
            Page<DbObjectSchema> pagedResult = crudService.findAll(entityName, page, size, filters, sortParams);
            log.info("pagedResult {}", pagedResult.getContent());
            List<String> searchableFieldNames = schema.getSearchableFields().stream().map(Field::getName).toList();
            List<String> sortableFieldNames = schema.getSortableFields().stream().map(Field::getName).toList();
            List<String> filterFieldNames = schema.getFilterableFields().stream().map(Field::getName).toList();
            List<String> filterableFieldNames = filterFields.stream()
                    .map(Field::getName) // This gets just "gender", "action", etc.
                    .collect(Collectors.toList());

            model.addAttribute("filterableFieldNames", filterableFieldNames);

            String primaryKey = "";
            DbObjectSchema firstItem = null;
            String sortBy = filters.getOrDefault("sortBy", "defaultField");
            String sortOrder = filters.getOrDefault("sortOrder", "asc");
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortOrder", sortOrder);
//            log.info("schema {}", schema);
            log.info("sortfieldnames {}", sortableFieldNames);
            log.info("filterFieldnames {}", filterFieldNames);
            log.info("searchfieldname {}", searchableFieldNames);

            if (!pagedResult.getContent().isEmpty()) {
                firstItem = pagedResult.getContent().get(0);
                List<String> fieldNames = new ArrayList<>(firstItem.getFieldsWithData().keySet());
                primaryKey = firstItem.getPrimaryKey();
                for (DbObjectSchema item : pagedResult.getContent()) {
                    processedItems.add(item.getFieldsWithData());
                    Map<String, Object> rawFields = item.getFieldsWithData();
                    Map<String, FieldValue> displayItem = new LinkedHashMap<>();

                    for (Map.Entry<String, Object> entry : rawFields.entrySet()) {
                        String fieldName = entry.getKey();
                        Object value = entry.getValue();

                        FieldType type = getDataType(value);
                        FieldValue fieldValue = new FieldValue();
                        fieldValue.setFieldType(type);
                        fieldValue.setValue(value);

                        displayItem.put(fieldName, fieldValue);
                    }

                    displayMap.add(displayItem);
                }

                model.addAttribute("items", processedItems);  // Processed data
                model.addAttribute("displayItems", displayMap);
                model.addAttribute("fieldNames", fieldNames);
                log.info("Primary key in controller {}", primaryKey);
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
//            log.info("enum fields {}", enumFields);
            model.addAttribute("enumFields", enumFields);
            model.addAttribute("entityName", entityName);
            model.addAttribute("primaryKey", primaryKey);
//            model.addAttribute("size", size);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", pagedResult.getTotalPages());
            model.addAttribute("totalElements", pagedResult.getTotalElements());
//            model.addAttribute("size", pagedResult.getContent().size());
            model.addAttribute("pagination", new PageResponse(pagedResult.getContent(), pagedResult.getNumber(), pagedResult.getTotalElements(), pagedResult.getTotalPages()));
            model.addAttribute("searchableFieldNames", searchFields);
            model.addAttribute("sortableFieldNames", sortableFieldNames);
            model.addAttribute("filterableFieldNames", filterFieldNames);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("formats", List.of("csv", "json", "xml"));
            model.addAttribute("sortOrder", sortOrder);
            model.addAttribute("fields", formFields);
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Read, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), entityName));
            log.info("processedItems {}", displayMap);
            processedItems.forEach(item1 -> {
                log.info("item1 key {} value {}", item1.keySet(), item1.values());
                item1.values().forEach(value1 -> {
//                    log.info("data type of value1 {}", getDataType(value1));
                });
            });
            log.info("display map entry set", displayMap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("error", "Error fetching data " + e.getMessage());
            applicationEventPublisher.publishEvent(new UIEvent(this, "ERROR FETCHING DATA", Status.Error));
        }
        return "crud/list";
    }

//    private FieldType getDataType(Object value1) {
//        if(value1.toString().contains(".jpg") || value1.toString().contains(".png")){
//            return FieldType.IMAGE;
//        } else if (value1.toString().contains(".pdf")) {
//            return FieldType.DOCUMENT;
//        } else if (value1.toString().contains(".mp4")) {
//            return FieldType.VIDEO;
//        } else if (value1.toString().contains(".mp3")) {
//            return FieldType.AUDIO;
//        }
//        return FieldType.TEXT;
//    }

    private FieldType getDataType(Object value) {
        if (value == null) return FieldType.TEXT;

        String val = value.toString().toLowerCase();

        if (val.endsWith(".jpg") || val.endsWith(".jpeg") || val.endsWith(".png") || val.endsWith(".gif")) {
            return FieldType.IMAGE;
        } else if (val.endsWith(".pdf") || val.endsWith(".doc") || val.endsWith(".docx")) {
            return FieldType.DOCUMENT;
        } else if (val.startsWith("http")) {
            return FieldType.LINK;
        } else {
            return FieldType.TEXT;
        }
    }


    // render update item page
    @GetMapping("/{entityName}/edit/{id}")
    public String editForm(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            Model model
    ) {
        try {
            Class<?> entityClass = entitiesScanner.getEntityByName(entityName);
            Optional<DbObjectSchema> item = crudService.findById(entityName, id);
            Map<String, String> validationErrors = (Map<String, String>) model.getAttribute("validationErrors");

            if (item.isPresent()) {
                log.info("item in controller ${}", item);
                Object entityInstance = item.get().getEntity(); // Get the actual entity instance
                System.out.println("Item " + item.get().getFieldsWithData());
                List<FormField> formFields = InputGenerator.generateFormInput(entityClass, item.get(), "/admin/" + entityName + "/edit", true, false, validationErrors);
                model.addAttribute("fields", formFields);
                model.addAttribute("entityName", entityName);
            } else {
                throw new RuntimeException("Entity with ID " + id + " not found.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching entity for editing: " + e.getMessage());
            model.addAttribute("error", "Error fetching entity " + e.getMessage());
        }

        return "crud/form";
    }

    // update item in the db
    @PostMapping("/{entityName}/edit/{id}")
    public String update(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            @RequestParam Map<String, String> formValues,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Class<?> entityInstance = entitiesScanner.getEntityByName(entityName);
        try {
            Optional<DbObjectSchema> item = crudService.findById(entityName, id);
            if (item.isPresent()) {
                DbObjectSchema dbObjectSchema = new DbObjectSchema(entityInstance, item.get().getEntity());
                Map<String, String> validationErrors = dbObjectSchema.validateFormValues(formValues);

                if (!validationErrors.isEmpty()) {
                    redirectAttributes.addFlashAttribute("validationErrors", validationErrors);
                    redirectAttributes.addFlashAttribute("formValues", formValues);
                    return "redirect:/admin/" + entityName + "/edit";
                }
                // Save the updated entity
                Object savedEntity = crudService.save(entityName, formValues, item.get().getEntity());
                System.out.println("✅ Entity updated successfully: " + savedEntity);
                applicationEventPublisher.publishEvent(new UIEvent(this, "Data updated successfully", Status.Success));
//                applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionEvent.UserActionType.Update, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), entityName));
            } else {
                System.err.println("Entity not found for ID: " + id);
                applicationEventPublisher.publishEvent(new UIEvent(this, "Data not found", Status.Error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error updating entity " + e.getMessage());
            applicationEventPublisher.publishEvent(new UIEvent(this, "Error updating data", Status.Error));
        }

        return "redirect:/admin/" + entityName;
    }


    // delete item from db
    @GetMapping("/{entityName}/delete/{id}")
    public String deleteItem(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            Model model
    ) {
        try {
            Optional<DbObjectSchema> item = crudService.findById(entityName, id);
            if (item.isPresent()){
                crudService.deleteById(entityName, id);
                applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Delete, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), entityName));
            }else{
                System.err.println("deleted entity: ");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting entity " + e.getMessage());
        }
        return "redirect:/admin/" + entityName;
    }

    // bulk operations
    @GetMapping("/{entityName}/bulk-action")
    public String bulkOperations(
            @PathVariable("entityName") String entityName,
            @RequestParam(name = "action") String bulkAction,
            @RequestParam(name= "selectedIds") List<String> selectedIds,
//            RedirectAttributes redirectAttributes,
            Model model
    ){
        System.out.println("Inside bulk operations");
        log.info("action {} ids {}", bulkAction, selectedIds);
//        if ("delete".equalsIgnoreCase(action)) {
//            // Example: delete from database
//            myService.bulkDelete(entityName, selectedIds);
//            redirectAttributes.addFlashAttribute("success", "Items deleted successfully.");
//            return "redirect:/admin/" + entityName;
//        }
//
//        if ("export".equalsIgnoreCase(action)) {
//            // Example: return a downloadable CSV
//            String csv = myService.generateCsvForIds(entityName, selectedIds);
//            response.setContentType("text/csv");
//            response.setHeader("Content-Disposition", "attachment; filename=export.csv");
//            response.getWriter().write(csv);
//            response.getWriter().flush();
//            return null;
//        }
//
//        redirectAttributes.addFlashAttribute("error", "Unknown bulk action.");
        return "redirect:/admin/" + entityName;
    }


    /**
     * export entity data in defined format
      */
    @GetMapping("/{entityName}/export")
    public ResponseEntity<byte[]> exportEntityData(
            @RequestParam(defaultValue = "json") String format,
            @PathVariable("entityName") String entityName
    ){
        try {
            List<Map<String, Object>> data = crudService.findAll(entityName, 0, 100, new HashMap<>(), new ArrayList<>()).getContent().stream().map(DbObjectSchema::getAllFieldsWithData).toList();
            DataExporter exporter = dataExporterRegistry.getExporter(format);
            byte[] content = exporter.export(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(exporter.getContentType()));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(entityName + exporter.getFileExtension()).build());
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * download file
      */
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
//        Resource file = fileStorageService.download(filename);
        Resource file = null;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(file);
    }

    
}
