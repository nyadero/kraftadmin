package com.bowerzlabs.models.kraftmodels;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.annotations.InternalAdminResource;
import com.bowerzlabs.annotations.KraftAdminField;
import com.bowerzlabs.annotations.KraftAdminResource;
import com.bowerzlabs.constants.PerformableAction;
import com.bowerzlabs.constants.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "kraft_admin_users")
@KraftAdminResource(name = "Administrators", rolesAllowed = {Role.SUPER_ADMIN}, actions = {PerformableAction.CREATE, PerformableAction.READ, PerformableAction.DELETE})
@InternalAdminResource
public class AdminUser implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public AdminUser(Long id, String username, String name, String password, Role role) {
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

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PostUpdate
    public void postUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public AdminUser(Long id, String username, String name, String password, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public AdminUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", avatar='" + avatar + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
