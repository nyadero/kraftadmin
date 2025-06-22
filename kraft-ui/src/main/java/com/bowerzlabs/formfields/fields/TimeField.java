package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.formfields.FormField;

import java.time.LocalTime;
import java.util.Map;

public class TimeField extends FormField {
    private String label;
    private final String placeholder;
    private final boolean required;
    private final LocalTime value;
    private String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public TimeField(String label, String placeholder, boolean required, LocalTime value, String name, Map<String, String> validationErrors, Map<String, String> validationRules) {
        super();
        this.label = formatLabel(label);
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
        return "time";
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
        return required;
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

//    /**
//     * @param label
//     * @param type
//     * @param placeholder
//     * @param name
//     * @param value
//     * @param required
//     * @param validationErrors
//     * @return
//     */
//    @Override
//    public String toHtml(String label, String type, String placeholder, String name, Object value, boolean required, Map<String, String> validationErrors) {
//        return "<label>" + label + ": <input type='" + type + "' name='" + name + "' value='" +
//                (value != null ? value : "") + "' placeholder='" + placeholder + "' required='" + required + "'></label><br/>";
//    }

    /**
     * builds the input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of();
    }
}
