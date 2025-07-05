package com.bowerzlabs.actions;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.EntityMetaModel;
import com.bowerzlabs.InputGenerator;
import com.bowerzlabs.constants.Status;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.dtos.FieldValue;
import com.bowerzlabs.dtos.PageResponse;
import com.bowerzlabs.events.UIEvent;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.service.CrudService;
import com.bowerzlabs.utils.DisplayUtils;
import com.bowerzlabs.utils.KraftUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
//@AdminController
public class KraftActionsController {
    public static final String ENTITY_NAME = "AdminUserActions";
    private final CrudService crudService;
    private static final Logger log = LoggerFactory.getLogger(KraftActionsController.class);
    private final EntitiesScanner entitiesScanner;
    private final ApplicationEventPublisher applicationEventPublisher;


    public KraftActionsController(CrudService crudService, EntitiesScanner entitiesScanner, ApplicationEventPublisher applicationEventPublisher) {
        this.crudService = crudService;
        this.entitiesScanner = entitiesScanner;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // render actions page
    @GetMapping("/actions")
    public String renderActions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Map<String, String> filters,
            @RequestParam(name = "sort", required = false) List<String> sortParams,
            Model model
    ){
        EntityMetaModel clazz = entitiesScanner.getEntityByName(ENTITY_NAME);
        try {
            DbObjectSchema schema = new DbObjectSchema(clazz,null);
            List<FormField> formFields = InputGenerator.generateFormInput(clazz, schema, "/admin/" + ENTITY_NAME + "/create", false, true, new HashMap<>());
            List<Field> sortFields = schema.getSortableFields();
            List<Field> filterFields = schema.getFilterableFields();
            List<Field> searchFields = schema.getSearchableFields();
            List<Map<String, Object>> processedItems = new ArrayList<>();
            List<Map<String, FieldValue>> displayMap = new ArrayList<>();
            Map<String, String> cleanedFilters = new HashMap<>();
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                if (entry.getKey().startsWith("filter.")) {
                    String fieldKey = entry.getKey().substring("filter.".length());
                    cleanedFilters.put(fieldKey, entry.getValue());
                }
            }
            filters = cleanedFilters;
            Page<DbObjectSchema> pagedResult = crudService.findAll(ENTITY_NAME, page, size, filters);
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

                        FieldValue displayField = DisplayUtils.resolveFieldValue(value);

                        displayItem.put(fieldName, displayField);
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
            model.addAttribute("entityName", ENTITY_NAME);
            model.addAttribute("primaryKey", KraftUtils.formatLabel(primaryKey));
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
//            applicationEventPublisher.publishEvent(new UserActionEvent(this, UserActionType.Read, (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), firstItem));
            log.info("processedItems {}", displayMap);
            processedItems.forEach(item1 -> {
                log.info("item1 key {} value {}", item1.keySet(), item1.values());
                item1.values().forEach(value1 -> {
//                    log.info("data type of value1 {}", getDataType(value1));
                });
            });
            log.info("display map entry set", displayMap);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching data " + e.getMessage());
            applicationEventPublisher.publishEvent(new UIEvent(this, "ERROR FETCHING DATA", Status.Error));
        }
        return "actions/index";
    }

}
