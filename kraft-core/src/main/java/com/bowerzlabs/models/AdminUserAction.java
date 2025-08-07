package com.bowerzlabs.models;

import com.bowerzlabs.annotations.DisplayField;
import com.bowerzlabs.annotations.InternalAdminResource;
import com.bowerzlabs.annotations.KraftAdminResource;
import com.bowerzlabs.constants.UserActionType;
import com.bowerzlabs.dtos.Subject;
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
@KraftAdminResource(
        name = "UserActions",
        group = "",
        icon = "\uD83D\uDCCA",
        editable = false,
        manageable = false
)
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
     * The table name of the Entity class and the id of the item this operation occurred on.
     */
    @Column(name = "subject", nullable = false)
    @Embedded
//    @DisplayField("dataId")
    private Subject subject;

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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
