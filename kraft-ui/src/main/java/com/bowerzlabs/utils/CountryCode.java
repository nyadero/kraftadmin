package com.bowerzlabs.utils;

public class CountryCode {
    private String name;
    private String code;
    private String dialCode;
    private String currencyCode;

    public CountryCode(String name, String code, String telPrefix, String currencyCode) {
        this.name = name;
        this.code = code;
        this.dialCode = telPrefix;
        this.currencyCode = currencyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return "CountryCode{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", dialCode='" + dialCode + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
