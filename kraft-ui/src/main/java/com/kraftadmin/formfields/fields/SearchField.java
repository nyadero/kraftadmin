package com.kraftadmin.formfields.fields;

import com.kraftadmin.formfields.FormField;

import java.util.Map;

public class SearchField extends FormField {
    private String name;
    private String label;
    public SearchField() {
        super();
    }

    /**
     * @return
     */
    @Override
    public String getLabel() {
        return "";
    }

    /**
     * @return
     */
    @Override
    public String getType() {
        return "search";
    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return "";
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
    public Object getValue() {
        return null;
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
     * builds the input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of();
    }
}
