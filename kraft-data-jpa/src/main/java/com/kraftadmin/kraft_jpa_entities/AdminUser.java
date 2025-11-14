package com.kraftadmin.kraft_jpa_entities;

import com.kraftadmin.annotations.FormInputType;
import com.kraftadmin.annotations.InternalAdminResource;
import com.kraftadmin.annotations.KraftAdminField;
import com.kraftadmin.annotations.KraftAdminResource;
import com.kraftadmin.constants.PerformableAction;
import com.kraftadmin.constants.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "kraft_admin_users")
@KraftAdminResource(
        name = "Administrators",
        rolesAllowed = {Role.SUPER_ADMIN},
        actions = {PerformableAction.CREATE, PerformableAction.READ, PerformableAction.DELETE})
@InternalAdminResource
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
//@ConditionalOnKraftJPASupported
@ConditionalOnProperty(name = "kraft.kraft-admin.primaryDB", havingValue = "JPA")
@Builder
public class AdminUser implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    @Email(message = "not a valid email")
    @NotBlank(message = "Email is required")
    private String username;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should have at least 8 characters")
    @KraftAdminField(showInTable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @FormInputType(FormInputType.Type.FILE)
    private String avatar;

    @OneToMany(mappedBy = "adminUser", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @KraftAdminField(showInTable = false)
    private List<AdminUserAction> adminUserActions;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public AdminUser(String id, String username, String name, String password, Role role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public AdminUser(String name, String email, String password, String avatar) {
        this.name = name;
        this.username = email;
        this.password = password;
        this.avatar = avatar;
    }

    public AdminUser(String id, String username, String name, String password, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PostUpdate
    public void postUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

}
