package com.bowerzlabs.formfields;

import java.util.Map;

public class FileInput extends FormField{
    private String label;
    private final String placeholder;
    private final boolean required;
    private final Object value;
    private String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public FileInput(String label, String placeholder, boolean required, Object value, String name, Map<String, String> validationErrors, Map<String, String> validationRules) {
        super();
        this.label = label;
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
        return "file";
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
    public Object getValue() {
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
