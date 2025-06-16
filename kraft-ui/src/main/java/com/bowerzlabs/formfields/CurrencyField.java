package com.bowerzlabs.formfields;

import com.bowerzlabs.utils.CountryCode;
import com.bowerzlabs.utils.CountryCodesUtil;

import java.util.List;
import java.util.Map;

public class CurrencyField extends FormField {
    private final String label;
    private final String placeholder;
    private final boolean required;
    private final String value;
    private final String name;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;
    private final List<CountryCode> countryCodeList;

    public CurrencyField(String label, String placeholder, boolean required, String value, String name, Map<String, String> validationErrors, Map<String, String> validationRules) {
        this.label = label;
        this.placeholder = placeholder;
        this.required = required;
        this.value = value;
        this.name = name;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
        this.countryCodeList = CountryCodesUtil.getCountryCodeList();
    }

    /**
     * returns the form-field label
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * returns the form-field type based on the entity field type
     */
    @Override
    public String getType() {
        return "currency";
    }

    /**
     * returns the form-field name based on the entity field name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * returns the form-field placeholder
     */
    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * returns the form-field required state
     */
    @Override
    public boolean getRequired() {
        return false;
    }

    /**
     * returns the form-field value from the field
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * returns the form-field validation errors
     */
    @Override
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    /**
     * returns the form-field validation errors
     */
    @Override
    public Map<String, String> getValidationRules() {
        return validationRules;
    }

    public List<CountryCode> getCountryCodeList() {
        return countryCodeList;
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
                "value", value,
                "required", required,
                "type", getType(),
                "countryCodes", countryCodeList,
                "validationRules", validationRules,
                "validationErrors", validationErrors
        );
    }


    @Override
    public String toString() {
        return "CurrencyField{" +
                "label='" + label + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", required=" + required +
                ", value='" + value + '\'' +
                ", name='" + name + '\'' +
                ", validationErrors=" + validationErrors +
                ", validationRules=" + validationRules +
                ", countryCodeList=" + countryCodeList +
                '}';
    }
}
