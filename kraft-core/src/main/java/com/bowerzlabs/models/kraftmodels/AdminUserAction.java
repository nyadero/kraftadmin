package com.bowerzlabs.models.kraftmodels;

import com.bowerzlabs.annotations.DisplayField;
import com.bowerzlabs.annotations.InternalAdminResource;
import com.bowerzlabs.constants.UserActionType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
 * A read/write operation executed by a user from the web UI. This class
 * only holds metadata about the operation and not anything
 * concrete yet (e.g. a diff or SQL query) about what change was performed.
 */
@Entity
@Table(name = "kraft_user_actions")
//@KraftAdminResource(name = "User Actions", group = "Admin", icon = "\uD83D\uDCCA", editable = true)
@InternalAdminResource
public class AdminUserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * The type of action being performed
     */
    @Enumerated(EnumType.STRING)
    private UserActionType action;

    /*
     * The admin user performing the action
     */
    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    @DisplayField("name")
    private AdminUser adminUser;

    /*
     * The table name of the Entity class this operation occurred on.
     */
    @Column(name = "table_name", nullable = false)
    private String table;

    /*
     * The time at which the operation was perfomed
     */
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public AdminUserAction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserActionType getAction() {
        return action;
    }

    public void setAction(UserActionType action) {
        this.action = action;
    }

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
