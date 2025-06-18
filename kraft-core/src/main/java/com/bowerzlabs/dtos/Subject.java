package com.bowerzlabs.dtos;

import jakarta.persistence.Embeddable;

import java.util.List;

/**
 * The table name of the Entity class and the id of the item this operation occurred on.
 */
@Embeddable
public class Subject {
    private String tableName;
    private List<String> dataId;

    public Subject(String tableName, List<String> dataId) {
        this.tableName = tableName;
        this.dataId = dataId;
    }

    public Subject() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getDataId() {
        return dataId;
    }

    public void setDataId(List<String> dataId) {
        this.dataId = dataId;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "tableName='" + tableName + '\'' +
                ", dataId='" + dataId + '\'' +
                '}';
    }
}
