package com.bowerzlabs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kraft")
public class KraftProperties {
/**
 * Whether kraft-admin is enabled
 * Default false
 * */
    private boolean enabled = false;
    /**
     * The title/name of your application
     * Default "Admin Dashboard"
     * */
    private String title = "Admin Dashboard";
    /**
     * The username of your dashboard's superuser/admin
     * Default "admin@kraftadmin.com"
     * */
    private String adminUsername = "admin@kraftadmin.com";
    /**
     * The superuser/admin's name
     * Default "Nyadero Brian Odhiambo"
     * */
    private String adminName = "Nyadero Brian";
    /**
     * The superuser/admin's password
     * Advised to immediately change once superuser has been saved to db
     * Default "password"
     * */
    private String password = "password";
    /**
     * The base url endpoint your application will use to access the admin resources
     * Default "admin"
     * */
    private String baseUrl = "admin";
    private String type;
    private String name;
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
