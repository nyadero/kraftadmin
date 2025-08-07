package com.bowerzlabs.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class VisitorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ipAddress;
    private String userAgent;
    private String path;
    private String country;
    private String routeVisited;

//    @CreationTimestamp
private LocalDateTime visitedAt = LocalDateTime.now();
}
