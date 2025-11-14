package com.kraftadmin.formfields.fields;

import com.kraftadmin.formfields.FormField;

import java.util.Map;

public class TextAreaField extends FormField {
    private String label;
    private final String placeholder;
    private final boolean required;
    private final String value;
    private String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public TextAreaField(String label, String placeholder, boolean required, String value, String name, Map<String, String> validationErrors, Map<String, String> validationRules) {
        super();
        this.label = formatLabel(label);
        this.placeholder = placeholder;
        this.required = required;
        this.value = value;
        this.name = name;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
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
        return "textarea";
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
        return required;
    }

    /**
     * @return 
     */
    @Override
    public Object getValue() {
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
     * builds the input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of();
    }

}
