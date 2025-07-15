package com.bowerzlabs.formfields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class FormField {
    private String name;
    private String label;
    private Object value;
    private List<String> searchOperations = new ArrayList<>();
    public String wrapperClass = "wrapper";

    /**
    returns the form-field label
     */
    public abstract String getLabel();

    /**
     * returns the form-field type based on the entity field type
      */
    public abstract String getType();

    /**
     * returns the form-field name based on the entity field name
     */
    public abstract String getName();
    /**
     * returns the form-field placeholder
     */
    public abstract String getPlaceholder();
    /**
     * returns the form-field required state
     */
    public abstract boolean getRequired();

    /**
     * returns the form-field value from the field
     */
    public abstract Object getValue();

    /**
     *  returns the form-field validation errors
      */
    public abstract Map<String, String> getValidationErrors();

    /**
     *     returns the form-field validation errors
      */
    public abstract Map<String, String> getValidationRules();

    /**
     *  returns whether field is a relation in the database or not
      */
    public boolean isRelationship(){
        return false;
    }

    /**
     * builds the input model data
     */
    public abstract Map<String, Object> getModelData();

    /**
     * Format a field name into a human-readable label
     */
    public static String formatLabel(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return "";
        }

        // Replace dots and underscores with spaces first
        String result = fieldName.replaceAll("[._]", " ");

        // Insert spaces before uppercase letters (handling camelCase nicely)
        result = result.replaceAll("([a-z])([A-Z])", "$1 $2");

        // Normalize spaces
        result = result.replaceAll("\\s+", " ").trim();

        // Capitalize first letter
        if (!result.isEmpty()) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }

        return result;
    }

    public List<String> getSearchOperations() {
        return searchOperations;
    }

    public void setSearchOperations(List<String> searchOperations) {
        this.searchOperations = searchOperations;
    }

    /**
     *  setter for input-field name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  setter for input-field label
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }


    public String getWrapperClass() {
        return wrapperClass;
    }

    public void setWrapperClass(String wrapperClass) {
        this.wrapperClass = wrapperClass;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
