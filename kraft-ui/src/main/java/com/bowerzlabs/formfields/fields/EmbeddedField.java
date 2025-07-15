package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.formfields.FormField;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmbeddedField extends FormField {
    private final String label;
    public final List<FormField> formFieldList;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public EmbeddedField(String label, List<FormField> formFieldList, Map<String, String> validationErrors, Map<String, String> validationRules) {
        super();
        this.label = formatLabel(label);
        this.formFieldList = formFieldList;
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
        return "embedded";
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

    /**
     * builds teh input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        Map<String, Object> modelData = formFieldList.stream()
                .collect(Collectors.toMap(FormField::getLabel, FormField::getModelData));
        return modelData;
    }

    @Override
    public String toString() {
        return "EmbeddedField{" +
                "label='" + label + '\'' +
                ", formFieldList=" + formFieldList +
                ", validationErrors=" + validationErrors +
                ", validationRules=" + validationRules +
                '}';
    }
}
