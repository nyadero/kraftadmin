package com.kraftadmin.formfields.fields;

import com.kraftadmin.formfields.FormField;

import java.util.Map;

public class MultiSelectField extends FormField {
    /**
     * returns the form-field label
     */
    @Override
    public String getLabel() {
        return "";
    }

    /**
     * returns the form-field type based on the entity field type
     */
    @Override
    public String getType() {
        return "";
    }

    /**
     * returns the form-field name based on the entity field name
     */
    @Override
    public String getName() {
        return "";
    }

    /**
     * returns the form-field placeholder
     */
    @Override
    public String getPlaceholder() {
        return "";
    }

    /**
     * returns the form-field required state
     */
    @Override
    public boolean getRequired() {
        return false;
    }

    /**
     * returns the form-field displayField from the field
     */
    @Override
    public Object getValue() {
        return null;
    }

    /**
     * returns the form-field validation errors
     */
    @Override
    public Map<String, String> getValidationErrors() {
        return Map.of();
    }

    /**
     * returns the form-field validation errors
     */
    @Override
    public Map<String, String> getValidationRules() {
        return Map.of();
    }

    /**
     * builds the input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of();
    }
}
