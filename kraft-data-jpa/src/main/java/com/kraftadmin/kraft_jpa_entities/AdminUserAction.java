package com.kraftadmin.kraft_jpa_entities;

import com.kraftadmin.annotations.DisplayField;
import com.kraftadmin.annotations.InternalAdminResource;
import com.kraftadmin.annotations.KraftAdminResource;
import com.kraftadmin.constants.PerformableAction;
import com.kraftadmin.constants.UserActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
 * A read/write operation executed by a user from the web UI. This class
 * only holds metadata about the operation and not anything
 * concrete yet (e.g. a diff or SQL query) about what change was performed.
 */
@Setter
@Getter
@Entity
@Table(name = "kraft_user_actions")
@KraftAdminResource(
        name = "User Actions",
        actions = {PerformableAction.READ})
@InternalAdminResource
//@ConditionalOnKraftJPASupported
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
    private Subject subject;

    /*
     * The time at which the operation was perfomed
     */
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public AdminUserAction() {
    }

}
