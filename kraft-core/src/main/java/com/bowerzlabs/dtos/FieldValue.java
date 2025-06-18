package com.bowerzlabs.dtos;


import com.bowerzlabs.constants.FieldType;

public class FieldValue {
    private FieldType fieldType;
    private Object value;

    public FieldValue() {
    }

    public FieldValue(FieldType fieldType, Object value) {
        this.fieldType = fieldType;
        this.value = value;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "FieldValue{" +
                "fieldType=" + fieldType +
                ", value=" + value +
                '}';
    }
}
