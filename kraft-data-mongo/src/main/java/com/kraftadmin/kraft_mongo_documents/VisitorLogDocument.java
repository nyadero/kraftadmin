package com.kraftadmin.kraft_mongo_documents;

import com.kraftadmin.annotations.InternalAdminResource;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "kraft_visitors_logs")
@InternalAdminResource
public class VisitorLogDocument {

    @Id
    private String id;

    private String ipAddress;
    private String userAgent;
    private String path;
    private String country;
    private String routeVisited;

    private LocalDateTime visitedAt = LocalDateTime.now();
}
