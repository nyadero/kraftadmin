package com.kraftadmin.formfields.fields;

import com.kraftadmin.formfields.FormField;

import java.util.Map;

public class EmailField extends FormField {
    private String label;
    private final String placeholder;
    private final boolean required;
    private final String value;
    private String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public EmailField(String label, String placeholder, boolean required, String value, String name, Map<String, String> validationRules, Map<String, String> validationErrors) {
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
        return "email";
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
        return Map.of();
    }

    /**
     * @return 
     */
    @Override
    public Map<String, String> getValidationRules() {
        return Map.of();
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
        return Map.of();
    }
}
