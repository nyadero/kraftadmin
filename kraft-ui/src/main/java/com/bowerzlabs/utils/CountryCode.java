package com.bowerzlabs.utils;

public class CountryCode {
    private String name;
    private String code;
    private String telPrefix;

    public CountryCode(String name, String code, String telPrefix) {
        this.name = name;
        this.code = code;
        this.telPrefix = telPrefix;
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

    public String getTelPrefix() {
        return telPrefix;
    }

    public void setTelPrefix(String telPrefix) {
        this.telPrefix = telPrefix;
    }

    @Override
    public String toString() {
        return "CountryCode{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", telPrefix='" + telPrefix + '\'' +
                '}';
    }
}
