package com.kraftadmin.formfields.fields;

import com.kraftadmin.formfields.FormField;

import java.util.List;
import java.util.Map;

public class SelectField extends FormField {
    private String label;
    private String name;
    private final String placeholder;
    private final List<Object> options;
    private final Object value;
    private final boolean isRequired;
    private final String wrapperClass;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public SelectField(String label, String name, String placeholder, List<Object> options, Object value, boolean isRequired, Map<String, String> validationErrors, Map<String, String> validationRules) {
        super();
        this.label = formatLabel(label);
        this.name = name;
        this.placeholder = placeholder;
        this.options = options;
        this.value = value;
        this.isRequired = isRequired;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
        wrapperClass = getWrapperClass();
    }

    /**
     * @return input label
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
        return "select";
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
        return isRequired;
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

    @Override
    public String getWrapperClass() {
        return super.getWrapperClass();
    }

    /**
     * builds the input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of(
                "label", label,
                "placeholder", placeholder,
                "name", name,
                "displayField", value,
                "required", isRequired,
                "type", getType(),
                "validationRules", validationRules,
                "validationErrors", validationErrors,
                "options", options,
                "wrapperClass", wrapperClass
        );
    }


    public List<Object> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return "SelectField{" +
                "label='" + label + '\'' +
                ", name='" + name + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", options=" + options +
                ", displayField=" + value +
                ", isRequired=" + isRequired +
                ", wrapperClass='" + wrapperClass + '\'' +
                ", validationErrors=" + validationErrors +
                ", validationRules=" + validationRules +
                '}';
    }

    public record Option(String label, String value) {
    }
}
