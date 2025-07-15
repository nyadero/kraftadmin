package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.formfields.FormField;

import java.util.Map;

public class CheckboxField extends FormField {
    private String label;
    private final boolean value;
    private String name;
    private final boolean required;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public CheckboxField(
            String label, boolean value, String name, boolean required, Map<String, String> validationErrors, Map<String, String> validationRules
    ) {
        this.label = label;
        this.value = value;
        this.name = name;
        this.required = required;
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
        return "checkbox";
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
        return "";
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
    public Boolean getValue() {
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

    /**
     * returns whether field is a relation in the database or not
     */
    @Override
    public boolean isRelationship() {
        return false;
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
        return "CheckboxField{" +
                "label='" + label + '\'' +
                ", value=" + value +
                ", name='" + name + '\'' +
                ", required=" + required +
                ", validationErrors=" + validationErrors +
                ", validationRules=" + validationRules +
                '}';
    }
}
