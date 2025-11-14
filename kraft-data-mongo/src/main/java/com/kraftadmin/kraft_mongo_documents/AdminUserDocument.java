package com.kraftadmin.kraft_mongo_documents;

import com.kraftadmin.annotations.FormInputType;
import com.kraftadmin.annotations.InternalAdminResource;
import com.kraftadmin.annotations.KraftAdminField;
import com.kraftadmin.annotations.KraftAdminResource;
import com.kraftadmin.constants.PerformableAction;
import com.kraftadmin.constants.Role;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Builder
@Document(collection = "kraft_admin_users")
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
public class AdminUserDocument implements UserDetails, Serializable {

    @Id
    private String id;

    @Email(message = "not a valid email")
    @NotBlank(message = "Email is required")
    private String username;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should have at least 8 characters")
    private String password;

    private Role role;

    @FormInputType(FormInputType.Type.FILE)
    private String avatar;

    @DBRef(lazy = true)
    @KraftAdminField(showInTable = false)
    private List<AdminUserActionDocument> adminUserActions;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public AdminUserDocument(String name, String email, String password, String avatar) {
        this.name = name;
        this.username = email;
        this.password = password;
        this.avatar = avatar;
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
        return List.of(() -> "ROLE_" + this.role.name());
    }
}
