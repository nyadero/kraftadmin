package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.formfields.FormField;

import java.util.Map;

public class TextField extends FormField {
    private String label;
    private final String placeholder;
    private final boolean required;
    private Object value;
    private String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public TextField(String label, String placeholder, boolean required, Object value, String name, Map<String, String> validationRules, Map<String, String> validationErrors) {
        super();
        this.label = label;
        this.placeholder = placeholder;
        this.required = required;
        this.value = value;
        this.name = name;
        this.validationRules = validationRules;
        this.validationErrors = validationErrors;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getType() {
        return "text";
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

    @Override
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    @Override
    public Map<String, String> getValidationRules() {
        return validationRules;
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
                "value", value,
                "required", required,
                "type", getType(),
                "validationRules", validationRules,
                "validationErrors", validationErrors
        );
    }

    @Override
    public void setName(String s) {
       this.name = s;
    }

    @Override
    public void setLabel(String s) {
        this.label = s;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TextField{" +
                "label='" + label + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", required=" + required +
                ", value=" + value +
                ", name='" + name + '\'' +
                ", validationErrors=" + validationErrors +
                ", validationRules=" + validationRules +
                '}';
    }

}
