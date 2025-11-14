package com.kraftadmin.settings;

import com.kraftadmin.EntitiesScanner;
import com.kraftadmin.EntityMetaModel;
import com.kraftadmin.dtos.KraftAdminDisplayFieldPreferenceDto;
import com.kraftadmin.dtos.KraftAdminUserDto;
import com.kraftadmin.formfields.fields.CheckboxField;
import com.kraftadmin.service.KraftAdminDisplayFieldPreferenceService;
import com.kraftadmin.service.KraftAdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("isAuthenticated")
public class KraftSettingsController {
    private final EntitiesScanner entitiesScanner;
    //    private final KraftDisplayedFieldPreferenceRepository kraftDisplayedFieldPreferenceRepository;
//    private final KraftAdminUsersRepository kraftAdminUsersRepository;
    private final KraftAdminDisplayFieldPreferenceService kraftAdminDisplayFieldPreferenceService;
    private final KraftAdminUserService kraftAdminUserService;

    public KraftSettingsController(
            EntitiesScanner entitiesScanner, KraftAdminDisplayFieldPreferenceService kraftAdminDisplayFieldPreferenceService, KraftAdminUserService kraftAdminUserService
    ) {
        this.entitiesScanner = entitiesScanner;
        this.kraftAdminDisplayFieldPreferenceService = kraftAdminDisplayFieldPreferenceService;
        this.kraftAdminUserService = kraftAdminUserService;
    }

    // render customize entity display form
    @GetMapping("/settings/{entityName}/customize-entity-view")
    public String renderCustomizeEntityDisplay(
            @PathVariable("entityName") String entityName,
            Model model
    ) {
        EntityMetaModel entityClass = entitiesScanner.getEntityByName(entityName);

        KraftAdminDisplayFieldPreferenceDto displayedFieldsPreference = kraftAdminDisplayFieldPreferenceService
                .findById(entityName).orElse(new KraftAdminDisplayFieldPreferenceDto());
        List<String> selectedFields = displayedFieldsPreference.getFields() != null
                ? displayedFieldsPreference.getFields()
                : new ArrayList<>();

        List<CheckboxField> checkboxFields = new ArrayList<>();
        for (Field field : entityClass.getEntityClass().getJavaType().getDeclaredFields()) {
            field.setAccessible(true);
            boolean isChecked = selectedFields.contains(field.getName());
            checkboxFields.add(new CheckboxField(field.getName(), isChecked, field.getName(), true, new HashMap<>(), new HashMap<>()));
        }

        model.addAttribute("checkboxFields", checkboxFields);
        model.addAttribute("entityName", entityName);
        model.addAttribute("title", "Customize View: " + entityName);


        return "kraft-settings/customize-entity-view";
    }

    @PostMapping("/settings/{entityName}/customize-entity-view")
    public String saveDisplayPreferences(
            @PathVariable("entityName") String entityName,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
//            EntityType<?> entityClass = entitiesScanner.getEntityByName(entityName);
            EntityMetaModel entityClass = entitiesScanner.getEntityByName(entityName);

            List<String> selectedFields = new ArrayList<>();

            for (Field field : entityClass.getEntityClass().getJavaType().getDeclaredFields()) {
                if (request.getParameter(field.getName()) != null) {
                    selectedFields.add(field.getName());
                }
            }

            KraftAdminDisplayFieldPreferenceDto preferences = new KraftAdminDisplayFieldPreferenceDto();
            preferences.setId(entityName);
            preferences.setFields(selectedFields);

            kraftAdminDisplayFieldPreferenceService.save(preferences);
            redirectAttributes.addFlashAttribute("message", "Preferences saved successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Error saving preferences", e);
        }
        return "redirect:/admin/settings/" + entityName + "/customize-entity-view";
    }

    @GetMapping("/settings")
    public String renderSettings(
            Model model,
            @AuthenticationPrincipal KraftAdminUserDto adminUser
    ){
//        Optional<KraftAdminUserDto> adminUser = kraftAdminUserService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
//        adminUser.ifPresent(user -> model.addAttribute("adminUser", user));
        model.addAttribute("adminUser", adminUser);
        log.info("adminUser {} {}", adminUser, SecurityContextHolder.getContext().getAuthentication());
        return "kraft-settings/index";
    }


}
