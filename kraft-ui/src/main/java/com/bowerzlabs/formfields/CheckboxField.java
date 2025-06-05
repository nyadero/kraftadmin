package com.bowerzlabs.formfields;

import java.util.Map;

public class CheckboxField extends FormField{
    private String label;
    private final Boolean value;
    private String name;
    private final boolean required;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;
    private final Boolean isRelation;

    public CheckboxField(
            String label, boolean value, String name, boolean required, Map<String, String> validationErrors, Map<String, String> validationRules, Boolean isRelation
    ) {
        this.label = label;
        this.value = value;
        this.name = name;
        this.required = required;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
        this.isRelation = isRelation;
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
        return false;
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
        return Map.of();
    }

    /**
     * @return
     */
    @Override
    public Map<String, String> getValidationRules() {
        return Map.of();
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
                "isRelation", isRelation,
                "validationRules", validationRules,
                "validationErrors", validationErrors
        );
    }


}
