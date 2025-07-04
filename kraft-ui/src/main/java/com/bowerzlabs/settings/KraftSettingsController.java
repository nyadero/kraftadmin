package com.bowerzlabs.settings;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.EntityMetaModel;
import com.bowerzlabs.formfields.fields.CheckboxField;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import com.bowerzlabs.models.kraftmodels.DisplayFieldsPreference;
import com.bowerzlabs.repository.kraftrepos.KraftAdminUsersRepository;
import com.bowerzlabs.repository.kraftrepos.KraftDisplayedFieldPreferenceRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Optional;

@Controller
@RequestMapping("/admin")
//@AdminController
public class KraftSettingsController {
    private final EntitiesScanner entitiesScanner;
    private final KraftDisplayedFieldPreferenceRepository kraftDisplayedFieldPreferenceRepository;
    private final KraftAdminUsersRepository kraftAdminUsersRepository;

    public KraftSettingsController(
            EntitiesScanner entitiesScanner,
            KraftDisplayedFieldPreferenceRepository kraftDisplayedFieldPreferenceRepository,
            KraftAdminUsersRepository kraftAdminUsersRepository
    ) {
        this.entitiesScanner = entitiesScanner;
        this.kraftDisplayedFieldPreferenceRepository = kraftDisplayedFieldPreferenceRepository;
        this.kraftAdminUsersRepository = kraftAdminUsersRepository;
    }

    // render customize entity display form
    @GetMapping("/settings/{entityName}/customize-entity-view")
    public String renderCustomizeEntityDisplay(
            @PathVariable("entityName") String entityName,
            Model model
    ) {
        EntityMetaModel entityClass = entitiesScanner.getEntityByName(entityName);

        DisplayFieldsPreference displayedFieldsPreference = kraftDisplayedFieldPreferenceRepository
                .findById(entityName).orElse(new DisplayFieldsPreference());
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


            DisplayFieldsPreference preferences = new DisplayFieldsPreference();
            preferences.setId(entityName);
            preferences.setFields(selectedFields);

            kraftDisplayedFieldPreferenceRepository.save(preferences);
            redirectAttributes.addFlashAttribute("message", "Preferences saved successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Error saving preferences", e);
        }

        return "redirect:/admin/settings/" + entityName + "/customize-entity-view";
    }

    @GetMapping("/settings")
    @PreAuthorize("isAuthenticated")
    public String renderSettings(
            Model model
    ){
        Optional<AdminUser> adminUser = kraftAdminUsersRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        adminUser.ifPresent(user -> model.addAttribute("adminUser", user));
        return "kraft-settings/index";
    }



}
