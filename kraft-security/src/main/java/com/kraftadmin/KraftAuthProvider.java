package com.kraftadmin;

import com.kraftadmin.dtos.KraftAdminUserDto;
import com.kraftadmin.service.KraftAdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
@Qualifier("kraftAuthProvider")
@ConditionalOnProperty(name = "kraft.kraft-admin.enabled", havingValue = "true")
//@ConditionalOnKraftAdminEnabled
public class KraftAuthProvider implements AuthenticationProvider {

    private final PasswordEncoder kraftPasswordEncoder;

    private final KraftAdminUserService kraftAdminUserService;

//    public KraftAuthProvider(PasswordEncoder kraftPasswordEncoder) {
//        this.kraftPasswordEncoder = kraftPasswordEncoder;
//    }

    @Autowired
    public KraftAuthProvider(
            @Qualifier("kraftPasswordEncoder") PasswordEncoder kraftPasswordEncoder, KraftAdminUserService kraftAdminUserService) {
        this.kraftPasswordEncoder = kraftPasswordEncoder;
        this.kraftAdminUserService = kraftAdminUserService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("Using KRAFT provider");
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        KraftAdminUserDto admin = kraftAdminUserService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!kraftPasswordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(
                admin,
                password,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name()))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
