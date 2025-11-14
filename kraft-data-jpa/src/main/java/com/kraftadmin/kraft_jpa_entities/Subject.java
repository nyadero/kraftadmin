package com.kraftadmin.kraft_jpa_entities;

import com.kraftadmin.annotations.DisplayField;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The table name of the Entity class and the id of the item this operation occurred on.
 */
@Getter
@Setter
@DisplayField("tableName")
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

    @Override
    public String toString() {
        return "Subject{" +
                "tableName='" + tableName + '\'' +
                ", dataId='" + dataId + '\'' +
                '}';
    }
}
