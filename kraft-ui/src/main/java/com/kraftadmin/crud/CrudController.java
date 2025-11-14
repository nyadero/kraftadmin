package com.kraftadmin.crud;

import com.kraftadmin.*;
import com.kraftadmin.annotations.KraftAdminResource;
import com.kraftadmin.components.KraftAdminCustomActionDiscovery;
import com.kraftadmin.constants.BulkAction;
import com.kraftadmin.constants.DataFormat;
import com.kraftadmin.constants.PerformableAction;
import com.kraftadmin.constants.UserActionType;
import com.kraftadmin.data.DataExporter;
import com.kraftadmin.data.DataExporterRegistry;
import com.kraftadmin.database.DbObjectSchema;
import com.kraftadmin.dtos.FieldValue;
import com.kraftadmin.dtos.KraftAdminAction;
import com.kraftadmin.dtos.KraftAdminUserDto;
import com.kraftadmin.dtos.PageResponse;
import com.kraftadmin.events.UserActionEvent;
import com.kraftadmin.files.LocalMultipartFileStorage;
import com.kraftadmin.files.MultipartFileStorage;
import com.kraftadmin.files.StorageProperties;
import com.kraftadmin.formfields.FormField;
import com.kraftadmin.kraft_jpa_entities.AdminUser;
import com.kraftadmin.kraft_jpa_entities.Subject;
import com.kraftadmin.service.CrudService;
import com.kraftadmin.service.KraftAdminActionExecutor;
import com.kraftadmin.service.KraftCrudService;
import com.kraftadmin.utils.DisplayUtils;
import com.kraftadmin.utils.KraftUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private MultipartFileStorage multipartFileStorage;
    @Autowired
    private StorageProperties storageProperties;
    private final KraftCrudService kraftCrudService;
    private final MongoDocumentScanner mongoDocumentScanner;
    @Autowired
    private KraftAdminCustomActionDiscovery actionDiscovery;
    @Autowired
    private KraftAdminActionExecutor adminActionExecutor;

    public CrudController(
            EntitiesScanner entitiesScanner,
            ApplicationEventPublisher applicationEventPublisher,
            CrudService crudService,
            DataExporterRegistry dataExporterRegistry,
            KraftCrudService kraftCrudService,
            MongoDocumentScanner mongoDocumentScanner
    ) {
        this.entitiesScanner = entitiesScanner;
        this.applicationEventPublisher = applicationEventPublisher;
        this.crudService = crudService;
        this.dataExporterRegistry = dataExporterRegistry;
        this.kraftCrudService = kraftCrudService;
        this.mongoDocumentScanner = mongoDocumentScanner;
    }

    // render create item page
    @GetMapping("/{entityName}/create")
    public String renderCreateForm(
            @PathVariable("entityName") String entityName,
            @RequestParam Map<String, String> formData,
            @AuthenticationPrincipal KraftAdminUserDto adminUser,
            RedirectAttributes redirectAttributes,
            Model model
    ){
        try {
            EntityMetaModel clazz = entitiesScanner.getEntityByName(entityName);
            Map<String, String> validationErrors = (Map<String, String>) model.getAttribute("validationErrors");
            DbObjectSchema dbObjectSchema = new DbObjectSchema(clazz,null);
            if (clazz.getEntityClass().getJavaType().isAnnotationPresent(KraftAdminResource.class)) {
                KraftAdminResource kraftAdminResource = clazz.getEntityClass().getJavaType().getAnnotation(KraftAdminResource.class);
                if (!dbObjectSchema.getRoles().contains(adminUser.getRole())) {
                    redirectAttributes.addFlashAttribute("error", "Unauthorized to perform this operation");
                    return "redirect:/admin/crud/" + entityName;
                }

                if (!dbObjectSchema.getPerformableActions().contains(PerformableAction.CREATE)) {
                    redirectAttributes.addFlashAttribute("error", "Unauthorized to create " + entityName);
                    return "redirect:/admin/crud/" + entityName;
                }
            }
            List<FormField> formFields = InputGenerator.generateFormInput(clazz, dbObjectSchema, "/admin/crud/" + entityName + "/create", false, false, validationErrors);
            model.addAttribute("actionUrl", "/admin/crud/" + entityName + "/create");
            model.addAttribute("fields", formFields);
            model.addAttribute("entityName", entityName);
            assert validationErrors != null;
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

//            System.out.println("Received form values: " + formValues);
//            System.out.println("Received files: " + fileInputs);

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
//                        log.info("storage provider {} and directory {}", storageProperties.getProvider(), storageProperties.getUploadDir());
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
//            applicationEventPublisher.publishEvent(new UIEvent(this, savedEntity.getClass().getSimpleName() + " saved successfully", Status.Success));
            Subject subject = new Subject(dbObjectSchema.getEntityName(), List.of(dbObjectSchema.getIdValue().toString()));
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Create,
                    (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));

        } catch (Exception e) {
            log.info("creation error {}", e.toString());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to saveAdmin entity: " + e.getMessage());
//            applicationEventPublisher.publishEvent(new UIEvent(this, "Failed to saveAdmin entity", Status.Error));
            return "redirect:/admin/crud/" + entityName;
        }

        return "redirect:/admin/crud/" + entityName;
    }

    // get entity by ID dynamically
    @GetMapping("/{entityName}/{id}")
    public String getItem(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            Model model,
            @AuthenticationPrincipal AdminUser adminUser
    ) {
        List<Map<String, FieldValue>> displayMap = new ArrayList<>();
        try {
            Optional<DbObjectSchema> item =  crudService.findById(entityName, id);
            log.info("relation {}", kraftCrudService.loadRelation(entityName, id));
            assert item.isPresent();
            // Get available kraft-actions-logs for Project entity
            List<KraftAdminAction> actions = actionDiscovery.discoverActionsForEntity(item.get().getEntity().getClass());
            log.info("kraft-actions-logs {}", actions);
            log.info("fields {}, {}", item.get().getAllFields(), item.get().getDisplayFields());
            log.info("fieldLabels1 {}", item.get().getFieldLabels());
            Map<String, String> displayToFieldMap = new HashMap<>();
            for (Field field : item.get().getDisplayFields()) {
                String rawField = field.getName();
                displayToFieldMap.put(KraftUtils.formatLabel(rawField), rawField);
            }
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
            model.addAttribute("fieldLabels", item.get().getFieldLabels());

            model.addAttribute("displayFieldMap", displayToFieldMap);

            model.addAttribute("item", displayMap);
            model.addAttribute("actions", actions);
            model.addAttribute("id", id);
            Subject subject = new Subject(item.get().getEntityName(), List.of(item.get().getIdValue().toString()));
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Read, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching entity " + e.getMessage());
        }
        return "kraft-crud/details";
    }


    //    // render all items, use pagination
    @GetMapping("/{entityName}")
    public String fetchAll(
            @PathVariable("entityName") String entityName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Map<String, String> allParams1,
            @AuthenticationPrincipal AdminUser adminUser,
            Model model
    ) {
        EntityMetaModel entityInstance = entitiesScanner.getEntityByName(entityName);
        DocumentMetaModel documentMetaModel = mongoDocumentScanner.getDocumentByName(entityName);
//        log.info("entityInstance {}", entityInstance);
        try {
            DbObjectSchema schema = new DbObjectSchema(entityInstance,null);
            log.info("Roles allowed {}, permissions allowed {}", schema.getAllFields(), schema.getDisplayFields());
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
            log.info("pagedResult {}", pagedResult);
            List<String> sortableFieldNames = schema.getSortableFields().stream().map(Field::getName).toList();
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
//            model.addAttribute("fieldNames", displayFieldNames);
            model.addAttribute("formats", DataFormat.values());

            if (!pagedResult.getContent().isEmpty()) {
                firstItem = pagedResult.getContent().get(0);
                List<String> fieldsWithData = new ArrayList<>(firstItem.getFieldsWithData().keySet());
                primaryKey = firstItem.getPrimaryKey();
                model.addAttribute("fieldLabels", firstItem.getFieldLabels());
                model.addAttribute("fieldNamesMap", firstItem.getAllFields().stream().map(Field::getName).toList());
                log.info("fieldLabels {}", firstItem.getAllFields().stream().map(Field::getName).toList());
                Map<String, String> fieldLabels = firstItem.getFieldLabels();
                for (DbObjectSchema item : pagedResult.getContent()) {
                    processedItems.add(item.getFieldsWithData());
                    Map<String, Object> rawFields = item.getFieldsWithData();
                    Map<String, FieldValue> displayItem = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> entry : rawFields.entrySet()) {
                        String fieldName = entry.getKey();
                        Object value = entry.getValue();
                        // Use fieldName to get the label from fieldLabels map
                        String fieldLabel = fieldLabels.get(fieldName.toLowerCase()); // e.g., "Custom Label"
                        FieldValue displayField = DisplayUtils.resolveFieldValue(value);
                        log.info("Field: {} | Label: {} | Value: {}", fieldName, fieldLabel, value);
                        // Store with label as key instead of field name if needed
                        displayItem.put(fieldLabel != null ? fieldLabel : fieldName, displayField);
                    }
                    idValues.add(item.getIdValue().toString());
                    displayMap.add(displayItem);
                }

                model.addAttribute("items", processedItems);  // Processed data
                model.addAttribute("displayItems", displayMap);
                model.addAttribute("fieldsWithData", fieldsWithData);
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
                    .filter(e -> !e.getKey().equals("page"))
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            model.addAttribute("queryString", "&" + queryString);
            model.addAttribute("currentParams", allParams); // for hidden inputs
            model.addAttribute("enumFields", enumFields);
            model.addAttribute("entityName", entityName);
            model.addAttribute("primaryKey", KraftUtils.formatLabel(primaryKey));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", pagedResult.getTotalPages());
            model.addAttribute(
                    "actions",
                    schema.getPerformableActions().stream().map(Enum::name).toList()
            );
            model.addAttribute("totalElements", pagedResult.getTotalElements());
            model.addAttribute("pagination", new PageResponse(pagedResult.getContent(), pagedResult.getNumber(), pagedResult.getTotalElements(), pagedResult.getTotalPages()));
            model.addAttribute("searchableFieldNames", searchFields);
            model.addAttribute("sortableFieldNames", sortableFieldNames);
            model.addAttribute("filterableFieldNames", filterFieldNames);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("roles", schema.getRoles());
            model.addAttribute("sortOrder", sortOrder);
//            model.addAttribute("fields", formFields);
            Subject subject = new Subject(schema.getEntityName(), idValues);
            log.info("filters {}, sortablefields {}", filters, sortableFieldNames);
            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Read, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
        } catch (Exception e) {
//            applicationEventPublisher.publishEvent(new UIEvent(this, "ERROR FETCHING DATA", Status.Error));
        }
        return "kraft-crud/list";
    }

// render all items, use pagination
//@GetMapping("/{entityName}")
//public String fetchAll(
//        @PathVariable("entityName") String entityName,
//        @RequestParam(defaultValue = "0") int page,
//        @RequestParam(defaultValue = "10") int size,
//        @RequestParam Map<String, String> allParams1,
//        @AuthenticationPrincipal AdminUser adminUser,
//        Model model
//) {
//    EntityMetaModel entityInstance = entitiesScanner.getEntityByName(entityName);
//    DocumentMetaModel documentMetaModel = mongoDocumentScanner.getDocumentByName(entityName);
//
//    log.info("EntityInstance: {}, DocumentMetaModel: {}",
//            entityInstance != null ? entityInstance.getEntityName() : "null",
//            documentMetaModel != null ? documentMetaModel.getCollectionName() : "null");
//
//    try {
//        // Create schema based on what type we found
//        DbObjectSchema schema;
//        if (entityInstance != null && documentMetaModel == null) {
//            // JPA Entity
//            log.debug("Creating DbObjectSchema with EntityMetaModel for: {}", entityName);
//            schema = new DbObjectSchema(entityInstance, null);
//        } else if (documentMetaModel != null && entityInstance == null) {
//            // MongoDB Document
//            log.debug("Creating DbObjectSchema with DocumentMetaModel for: {}", entityName);
////            schema = new DbObjectSchema(documentMetaModel, null);
//        } else if (entityInstance != null && documentMetaModel != null) {
//            // Both exist - prioritize JPA (or choose based on your business logic)
//            log.warn("Both EntityMetaModel and DocumentMetaModel found for: {}. Using JPA.", entityName);
//            schema = new DbObjectSchema(entityInstance, null);
//        } else {
//            // Neither exists
//            log.error("No EntityMetaModel or DocumentMetaModel found for: {}", entityName);
//            applicationEventPublisher.publishEvent(new UIEvent(this, "Entity/Document not found: " + entityName, Status.Error));
//            return "kraft-crud/list"; // or redirect to error page
//        }
//

    /// /        log.info("Roles allowed {}, permissions allowed {}", schema.getAllFields(), schema.getDisplayFields());
//
//        List<Field> filterFields = schema.getFilterableFields();
//        List<Field> searchFields = schema.getSearchableFields();
//        List<Map<String, Object>> processedItems = new ArrayList<>();
//        List<Map<String, FieldValue>> displayMap = new ArrayList<>();
//        List<PerformableAction> performableActions = new ArrayList<>();
//        List<String> idValues = new ArrayList<>();
//
//        Map<String, String> filters = new HashMap<>();
//        for (Map.Entry<String, String> entry : allParams1.entrySet()) {
//            if (entry.getKey().startsWith("filter.")) {
//                String fieldKey = entry.getKey().substring("filter.".length());
//                filters.put(fieldKey, entry.getValue());
//            }
//        }
//
//        Page<DbObjectSchema> pagedResult = crudService.findAllAdministrators(entityName, page, size, filters);
//        log.info("pagedResult {}", pagedResult);
//
//        List<String> sortableFieldNames = schema.getSortableFields().stream()
//                .map(field -> KraftUtils.formatLabel(field.getName())).toList();
//        List<String> filterFieldNames = schema.getFilterableFields().stream()
//                .map(Field::getName).toList();
//        List<String> filterableFieldNames = filterFields.stream()
//                .map(Field::getName)
//                .collect(Collectors.toList());
//
//        model.addAttribute("filterableFieldNames", filterableFieldNames);
//
//        String primaryKey = "";
//        DbObjectSchema firstItem = null;
//        String sortBy = filters.getOrDefault("sortBy", "id");
//        String sortOrder = filters.getOrDefault("sortOrder", "asc");
//        model.addAttribute("sortBy", sortBy);
//        model.addAttribute("sortOrder", sortOrder);
//
//        Map<String, String> displayToFieldMap = new HashMap<>();
//        for (Field field : schema.getSortableFields()) {
//            String rawField = field.getName();
//            displayToFieldMap.put(KraftUtils.formatLabel(rawField), rawField);
//        }
//        model.addAttribute("displayToFieldMap", displayToFieldMap);
//        List<String> displayFieldNames = displayToFieldMap.keySet().stream().toList();
//        model.addAttribute("fieldNames", displayFieldNames);
//        model.addAttribute("formats", DataFormat.values());
//
//        if (!pagedResult.getContent().isEmpty()) {
//            firstItem = pagedResult.getContent().get(0);
//            List<String> fieldNames = new ArrayList<>(firstItem.getFieldsWithData().keySet());
//            primaryKey = firstItem.getPrimaryKey();
//
//            for (DbObjectSchema item : pagedResult.getContent()) {
//                processedItems.add(item.getFieldsWithData());
//                Map<String, Object> rawFields = item.getFieldsWithData();
//                Map<String, FieldValue> displayItem = new LinkedHashMap<>();
//                for (Map.Entry<String, Object> entry : rawFields.entrySet()) {
//                    String fieldName = entry.getKey();
//                    Object value = entry.getValue();
//                    FieldValue displayField = DisplayUtils.resolveFieldValue(value);
//                    displayItem.put(fieldName, displayField);
//                }
//                idValues.add(item.getIdValue().toString());
//                displayMap.add(displayItem);
//            }
//
//            model.addAttribute("items", processedItems);
//            model.addAttribute("displayItems", displayMap);
//            model.addAttribute("fieldNames", fieldNames);
//        }
//
//        // Dynamically add enum values for each enum field
//        Map<String, List<String>> enumFields = new HashMap<>();
//        for (Field field : filterFields) {
//            if (field.getType().isEnum()) {
//                Class<?> enumType = field.getType();
//                List<String> enumValues = Arrays.stream(enumType.getEnumConstants())
//                        .map(enumConstant -> ((Enum<?>) enumConstant).name())
//                        .collect(Collectors.toList());
//                enumFields.put(field.getName(), enumValues);
//            }
//        }
//
//        Map<String, String> allParams = new HashMap<>(filters);
//        allParams.put("page", String.valueOf(page));
//
//        // Build a query string for pagination/sorting links
//        String queryString = allParams.entrySet().stream()
//                .filter(e -> !e.getKey().equals("page"))
//                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
//                .collect(Collectors.joining("&"));
//
//        model.addAttribute("queryString", "&" + queryString);
//        model.addAttribute("currentParams", allParams);
//        model.addAttribute("enumFields", enumFields);
//        model.addAttribute("entityName", entityName);
//        model.addAttribute("primaryKey", KraftUtils.formatLabel(primaryKey));
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", pagedResult.getTotalPages());
//        model.addAttribute(
//                "actions",
//                schema.getPerformableActions().stream().map(Enum::name).toList()
//        );
//        model.addAttribute("totalElements", pagedResult.getTotalElements());
//        model.addAttribute("pagination", new PageResponse(pagedResult.getContent(), pagedResult.getNumber(), pagedResult.getTotalElements(), pagedResult.getTotalPages()));
//        model.addAttribute("searchableFieldNames", searchFields);
//        model.addAttribute("sortableFieldNames", sortableFieldNames);
//        model.addAttribute("filterableFieldNames", filterFieldNames);
//        model.addAttribute("sortBy", sortBy);
//        model.addAttribute("roles", schema.getRoles());
//        model.addAttribute("sortOrder", sortOrder);
//
//        Subject subject = new Subject(schema.getEntityName(), idValues);
//        log.info("filters {}, sortablefields {}, displayItems {}", filters, sortableFieldNames, displayMap);
//        applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Read, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
//
//    } catch (Exception e) {
//        log.error("Error in fetchAll for entity: {}", entityName, e);
//        applicationEventPublisher.publishEvent(new UIEvent(this, "ERROR FETCHING DATA", Status.Error));
//    }
//    return "kraft-crud/list";
//}

    // render update item page
    @GetMapping("/{entityName}/edit/{id}")
    public String editForm(
            @PathVariable("entityName") String entityName,
            @PathVariable("id") String id,
            @AuthenticationPrincipal AdminUser adminUser,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            EntityMetaModel clazz = entitiesScanner.getEntityByName(entityName);
            DbObjectSchema dbObjectSchema = crudService.findById(entityName, id).get();
            if (clazz.getEntityClass().getJavaType().isAnnotationPresent(KraftAdminResource.class)) {
                KraftAdminResource kraftAdminResource = clazz.getEntityClass().getJavaType().getAnnotation(KraftAdminResource.class);
                if (!dbObjectSchema.getRoles().contains(adminUser.getRole())) {
                    redirectAttributes.addFlashAttribute("error", "Unauthorized to perform this action");
//                    applicationEventPublisher.publishEvent(new UIEvent(this, "Unauthorized to perform this operation", Status.Error));
                    return "redirect:/admin/crud/" + entityName;
                }
                if (!dbObjectSchema.getPerformableActions().contains(PerformableAction.EDIT)) {
                    redirectAttributes.addFlashAttribute("unauthorized", "Unauthorized");
//                    applicationEventPublisher.publishEvent(new UIEvent(this, "Unauthorized to perform this operation", Status.Error));
                    return "redirect:/admin/crud/" + entityName;
                }
                log.info("kraftAdmin resource {}", kraftAdminResource);


            }
            Map<String, String> validationErrors = (Map<String, String>) model.getAttribute("validationErrors");
            List<FormField> formFields = InputGenerator.generateFormInput(clazz, dbObjectSchema, "/admin/crud/" + entityName + "/edit", true, false, validationErrors);
            model.addAttribute("fields", formFields);
            model.addAttribute("entityName", entityName);
        } catch (Exception e) {
//            applicationEventPublisher.publishEvent(new UIEvent(this, e.getMessage(), Status.Error));
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
            @RequestParam(required = false) Map<String, MultipartFile> fileInputs,
            HttpServletRequest request,
            @AuthenticationPrincipal AdminUser adminUser,
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
                // Save the updated entity
                Object savedEntity = crudService.save(entityName, formValues, item.get().getEntity());
//                applicationEventPublisher.publishEvent(new UIEvent(this, entityName + " updated successfully", Status.Success));
                Subject subject = new Subject(item.get().getEntityName(), List.of(item.get().getIdValue().toString()));
                applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Update, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
            }
        } catch (Exception e) {
//            applicationEventPublisher.publishEvent(new UIEvent(this, "Error updating " + entityName, Status.Error));
//            return "redirect:/admin/crud/${entityName}/edit/${id}(entityName=${entityName},id=${id})" + entityName;
        }

        return "redirect:/admin/crud/" + entityName;
    }

    // delete item from db
    @GetMapping("/{entityName}/delete/{id}")
    public String deleteItem(
            @PathVariable("entityName") String entityName,
            @AuthenticationPrincipal AdminUser adminUser,
            @PathVariable("id") String id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            EntityMetaModel clazz = entitiesScanner.getEntityByName(entityName);
            Optional<DbObjectSchema> item = crudService.findById(entityName, id);
            if (clazz.getEntityClass().getJavaType().isAnnotationPresent(KraftAdminResource.class)) {
                KraftAdminResource kraftAdminResource = clazz.getEntityClass().getJavaType().getAnnotation(KraftAdminResource.class);
                if (!item.get().getRoles().contains(adminUser.getRole())) {
                    redirectAttributes.addFlashAttribute("error", "Unauthorized to perform this operation");
                    return "redirect:/admin/crud/" + entityName;
                }
                if (!item.get().getPerformableActions().contains(PerformableAction.DELETE)) {
                    redirectAttributes.addFlashAttribute("error", "Unauthorized to delete " + entityName);
                    return "redirect:/admin/crud/" + entityName;
                }
            }
            if (item.isPresent()){
                crudService.deleteById(entityName, id);
                Subject subject = new Subject(item.get().getEntityName(), List.of(item.get().getIdValue().toString()));
                applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Delete, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), subject));
//                applicationEventPublisher.publishEvent(new UIEvent(this, entityName + " deleted successfully", Status.Error));
            }
        } catch (Exception e) {
//            applicationEventPublisher.publishEvent(new UIEvent(this, "error deleting "+entityName, Status.Error));
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
            @AuthenticationPrincipal AdminUser adminUser,
            Model model
    ) throws Exception {
        log.info("action {} ids {}", bulkAction, selectedIds);
        switch (BulkAction.valueOf(bulkAction)){
            case Delete -> {
                crudService.bulkDelete(entityName, selectedIds);
                redirectAttributes.addFlashAttribute("success", "Items deleted successfully.");
                return "redirect:/admin/crud/" + entityName;
            }
            case Export -> {
                crudService.exportData(entityName, selectedIds, format);
                redirectAttributes.addFlashAttribute("success", "Items exported successfully.");
                return "redirect:/admin/crud/" + entityName;
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
            @AuthenticationPrincipal AdminUser adminUser,
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
//            applicationEventPublisher.publishEvent(new UIEvent(this, e.getMessage(), Status.Error));
            return "redirect:/admin/crud/" + entityName;
        }
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    /**
     * download file
      */
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(
            @PathVariable String filename,
            @AuthenticationPrincipal AdminUser adminUser
    ) {
        Resource file = null;
        try {
            file = multipartFileStorage.download(filename);
        } catch (Exception e) {
//            applicationEventPublisher.publishEvent(new UIEvent(this, e.getMessage(), Status.Error));
            log.info("error {}", e.toString());
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(file);
    }

    @PostMapping("/{entityName}/{id}/actions/execute")
    public String executeAction(
            @PathVariable() String entityName,
            @PathVariable(name = "id") String id,
            @RequestParam(name = "actionName") String actionName
//            @RequestParam List<Long> entityIds,
//            @RequestParam String entityType
    ) {
        try {
            Optional<DbObjectSchema> item = crudService.findById(entityName, id);
            assert item.isPresent();
            EntityMetaModel entityInstance = entitiesScanner.getEntityByName(entityName);
            log.info("entityInstance {}", entityInstance);
            adminActionExecutor.executeAction(actionName, item.get().getEntity().getClass());
            return "redirect:/admin/crud/" + entityName + "/" + id;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/kraft-actions-logs/execute1")
    public String executeAction(
            @RequestParam String actionName,
            @RequestParam List<Long> entityIds,
            @RequestParam String entityType,
            RedirectAttributes redirectAttributes) {

        try {
//            ActionResult result = adminActionExecutor.executeAction(actionName, entityIds, entityType);
//
//            if (result.isSuccess()) {
//                redirectAttributes.addFlashAttribute("successMessage", result.getMessage());
//            } else {
//                redirectAttributes.addFlashAttribute("errorMessage", result.getMessage());
//            }

        } catch (Exception e) {
            log.error("Action execution failed", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Action failed: " + e.getMessage());
        }

        // Redirect back to the admin page
        return "redirect:/admin/" + entityType.toLowerCase() + "s";
    }
    
}
