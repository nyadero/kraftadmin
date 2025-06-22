package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.formfields.FormField;

import java.util.Map;

public class PasswordField extends FormField {
    private String label;
    private final String placeholder;
    private final boolean required;
    private final String value;
    private String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public PasswordField(String label, String placeholder, boolean required, String value, String name, Map<String, String> validationRules, Map<String, String> validationErrors) {
        super();
        this.label = formatLabel(label);
        this.placeholder = placeholder;
        this.required = required;
        this.value = value;
        this.name = name;
        this.validationRules = validationRules;
        this.validationErrors = validationErrors;
    }

    /**
     * @return
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * @return
     */
    @Override
    public String getType() {
        return "password";
    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * @return
     */
    @Override
    public boolean getRequired() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * @return
     */
    @Override
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    /**
     * @return
     */
    @Override
    public Map<String, String> getValidationRules() {
        return validationRules;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public void setLabel(String s) {
        this.label = s;
    }

    /**
     * builds teh input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of(
                "label", label,
                "placeholder", placeholder,
                "name", name,
                "value", value,
                "required", required,
                "type", getType(),
                "validationRules", validationRules,
                "validationErrors", validationErrors
        );
    }
}
