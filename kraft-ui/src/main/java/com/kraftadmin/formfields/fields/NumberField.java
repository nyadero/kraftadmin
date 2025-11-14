package com.kraftadmin.formfields.fields;

import com.kraftadmin.formfields.FormField;

import java.util.Map;

public class NumberField extends FormField {
    private String label;
    private final String placeholder;
    private final Number value; // Supports int, long, float, double
    private String name;
    private final boolean required;
//    private final List<String> searchOperations;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public NumberField(String label, String placeholder, Number value, String name, boolean required, Map<String, String> validationErrors, Map<String, String> validationRules) {
        super();
        this.label = formatLabel(label);
        this.placeholder = placeholder;
        this.value = value;
        this.name = name;
        this.required = required;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getType() {
        return "number";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public boolean getRequired() {
        return required;
    }

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
        return Map.of(
                "label", label,
                "placeholder", placeholder,
                "name", name,
                "displayField", value,
                "required", required,
                "type", getType(),
                "validationRules", validationRules,
                "validationErrors", validationErrors
        );
    }
}
