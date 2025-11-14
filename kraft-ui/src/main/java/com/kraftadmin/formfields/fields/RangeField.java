package com.kraftadmin.formfields.fields;

import com.kraftadmin.formfields.FormField;

import java.util.HashMap;
import java.util.Map;

public class RangeField extends FormField {
    private final String label;
    private final String placeholder;
    private final boolean required;
    private final Number value;
    private final String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public RangeField(String label, String placeholder, boolean required, Number value, String name,
                      Map<String, String> validationErrors, Map<String, String> validationRules) {
        this.label = label;
        this.placeholder = placeholder;
        this.required = required;
        this.value = value;
        this.name = name;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
    }

    @Override public String getLabel() { return label; }
    @Override public String getType() { return "range"; }
    @Override public String getName() { return name; }
    @Override public String getPlaceholder() { return placeholder; }
    @Override public boolean getRequired() { return required; }
    @Override public Object getValue() { return value; }
    @Override public Map<String, String> getValidationErrors() { return validationErrors; }
    @Override public Map<String, String> getValidationRules() { return validationRules; }

    @Override
    public Map<String, Object> getModelData() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", getType());
        data.put("label", label);
        data.put("placeholder", placeholder);
        data.put("required", required);
        data.put("displayField", value);
        data.put("name", name);
        return data;
    }
}
