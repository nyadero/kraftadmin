package com.bowerzlabs.models.kraftmodels;

public class ResourceName {
    private  String key;
    private  String value;

    public ResourceName(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ResourceName{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

}
