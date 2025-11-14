package com.kraftadmin.kraft_jpa_entities;

import com.kraftadmin.annotations.InternalAdminResource;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "display_field_preferences")
//@KraftAdminResource(name = "Display Preferences", group = "Admin", icon = "", editable = false)
@InternalAdminResource
//@ConditionalOnKraftJPASupported
public class DisplayFieldsPreference {
    @Id
    private String id;

    @ElementCollection
    private List<String> fields;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "DisplayFieldsPreference{" +
                "id='" + id + '\'' +
                ", fields=" + fields +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
