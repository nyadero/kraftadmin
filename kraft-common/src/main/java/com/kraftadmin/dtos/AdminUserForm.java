package com.kraftadmin.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@RequiredArgsConstructor
public class AdminUserForm {

    private String id;

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    //    @NotBlank
    private String password;

    private String role;

    private MultipartFile avatar;

    @Override
    public String toString() {
        return "AdminUserForm{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", avatar=" + avatar +
                '}';
    }
}
