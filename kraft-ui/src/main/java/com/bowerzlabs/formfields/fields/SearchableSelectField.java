package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.formfields.FormField;

import java.util.Map;

public class SearchableSelectField extends FormField {
    private String label;
    private String name;
    private final String placeholder;
    private final Map<Object, Object> options;
    private final boolean required;
    private final Object value;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public SearchableSelectField(
            String label,
            String name,
            String placeholder,
            Map<Object, Object> options,
            Object value,
            boolean required,
            Map<String, String> validationErrors,
            Map<String, String> validationRules
    ) {
        super();
        this.label = formatLabel(label);
        this.name = name;
        this.placeholder = placeholder;
        this.options = options;
        this.required = required;
        this.value = value;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
    }

    @Override
    public String getLabel() {
        return label;
    }
    
    @Override
    public String getType() {
        return "searchable-select";
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

    public Map<Object, Object> getOptions() {
        return options;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    @Override
    public Map<String, String> getValidationRules() {
        return Map.of(); // Optional: you can inject rules too
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
                "placeholder", placeholder,
                "options", options,
                "required", required,
                "validationErrors", validationErrors,
                "validationRules", validationRules
        );
    }

    @Override
    public String toString() {
        return "SearchableSelectField{" +
                "label='" + label + '\'' +
                ", name='" + name + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", options=" + options +
                ", required=" + required +
                ", value=" + value +
                ", validationErrors=" + validationErrors +
                ", validationRules=" + validationRules +
                '}';
    }
}
