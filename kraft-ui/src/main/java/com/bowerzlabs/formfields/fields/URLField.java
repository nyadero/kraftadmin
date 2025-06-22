package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.formfields.FormField;

import java.util.Map;

public class URLField extends FormField {
    private String label;
    private final String placeholder;
    private final boolean required;
    private final String value;
    private String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public URLField(String label, String placeholder, boolean required, String  value, String name, Map<String, String> validationRules, Map<String, String> validationErrors) {
        super();
        this.label = formatLabel(label);
        this.placeholder = placeholder;
        this.required = required;
        this.value = value;
        this.name = name;
        this.validationRules = validationRules;
        this.validationErrors = validationErrors;
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
        return "url";
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
    public String getValue() {
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

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public void setLabel(String s) {
        this.label = s;
    }

//
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
//        StringBuilder html = new StringBuilder();
//
//        html.append("<label>").append(label).append(": ")
//                .append("<input type='").append(type).append("' name='").append(name).append("' value='")
//                .append(value != null ? value : "").append("' placeholder='").append(placeholder).append("'")
//                .append(required ? " required" : "").append("></label><br/>");
//
//        if (validationErrors != null && !validationErrors.isEmpty() && validationErrors.containsKey(name)) {
//            html.append("<span style='color:red;'>").append(validationErrors.get(name)).append("</span><br/>");
//        }
//
//        return html.toString();
//    }

    /**
     * builds the input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of(
                "label", label,
                "placeholder", placeholder,
                "name", name,
                "value", value,
                "required", required,
                "type", getType(),
                "validationRules", validationRules,
                "validationErrors", validationErrors
        );
    }
}
