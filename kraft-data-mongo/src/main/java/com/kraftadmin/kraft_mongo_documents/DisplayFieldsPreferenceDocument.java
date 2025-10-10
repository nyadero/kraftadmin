package com.kraftadmin.kraft_mongo_documents;

import com.kraftadmin.annotations.InternalAdminResource;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
//@Document(collection = "display_field_preferences")
@InternalAdminResource
public class DisplayFieldsPreferenceDocument {

    @Id
    private String id;

    private List<String> fields;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "DisplayFieldsPreferenceDocument{" +
                "id='" + id + '\'' +
                ", fields=" + fields +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

