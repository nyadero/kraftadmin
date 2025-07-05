package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.formfields.FormField;

import java.util.Map;

public class TagInput extends FormField {
    private final String placeholder;
    private final boolean required;
    private final Object value;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;
    private String label;
    private String name;

    public TagInput(String name, String label, String placeholder, boolean required, Object value,
                    Map<String, String> validationErrors, Map<String, String> validationRules) {
        super();
        this.name = name;
        this.label = label;
        this.placeholder = placeholder;
        this.required = required;
        this.value = value;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
    }

    /**
     * returns the form-field label
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * returns the form-field type based on the entity field type
     */
    @Override
    public String getType() {
        return "tags";
    }

    @Override
    public void setLabel(String s) {
        this.label = s;
    }

    /**
     * returns the form-field name based on the entity field name
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    /**
     * returns the form-field placeholder
     */
    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * returns the form-field required state
     */
    @Override
    public boolean getRequired() {
        return required;
    }

    /**
     * returns the form-field value from the field
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * returns the form-field validation errors
     */
    @Override
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    /**
     * returns the form-field validation errors
     */
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
    public String toString() {
        return "TagInput{" +
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
