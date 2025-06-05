package com.bowerzlabs.dtos;

import java.util.Map;

public class TableRowDTO {
    private Map<String, FieldValue> displayItem;
    private Map<String, Object> rawItem;

    public TableRowDTO(Map<String, FieldValue> displayItem, Map<String, Object> rawItem) {
        this.displayItem = displayItem;
        this.rawItem = rawItem;
    }

    public Map<String, FieldValue> getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(Map<String, FieldValue> displayItem) {
        this.displayItem = displayItem;
    }

    public Map<String, Object> getRawItem() {
        return rawItem;
    }

    public void setRawItem(Map<String, Object> rawItem) {
        this.rawItem = rawItem;
    }

    @Override
    public String toString() {
        return "TableRowDTO{" +
                "displayItem=" + displayItem +
                ", rawItem=" + rawItem +
                '}';
    }

}

