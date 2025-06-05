package com.bowerzlabs.formfields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadioField extends FormField{
    private final String label;
    private final String placeholder;
    private final boolean required;
    private final String value;
    private final String name;
    private final List<String> options;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public RadioField(String label, String placeholder, boolean required, String value, String name,
                      List<String> options,
                      Map<String, String> validationErrors, Map<String, String> validationRules) {
        this.label = label;
        this.placeholder = placeholder;
        this.required = required;
        this.value = value;
        this.name = name;
        this.options = options;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
    }

    @Override public String getLabel() { return label; }
    @Override public String getType() { return "radio"; }
    @Override public String getName() { return name; }
    @Override public String getPlaceholder() { return placeholder; }
    @Override public boolean getRequired() { return required; }
    @Override public Object getValue() { return value; }
    public List<String> getOptions() { return options; }
    @Override public Map<String, String> getValidationErrors() { return validationErrors; }
    @Override public Map<String, String> getValidationRules() { return validationRules; }

    @Override
    public Map<String, Object> getModelData() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", getType());
        data.put("label", label);
        data.put("placeholder", placeholder);
        data.put("required", required);
        data.put("value", value);
        data.put("name", name);
        data.put("options", options);
        return data;
    }
}
