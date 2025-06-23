package com.bowerzlabs.models.kraftmodels;

import java.time.LocalDateTime;

//@Entity
public class VisitorLog {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ipAddress;
    private String userAgent;
    private String routeVisited;

//    @CreationTimestamp
    private LocalDateTime visitedAt;
}
