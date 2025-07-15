package com.bowerzlabs;

import com.bowerzlabs.models.kraftmodels.AdminUser;
import com.bowerzlabs.repository.kraftrepos.KraftAdminUsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class KraftAuthProvider implements AuthenticationProvider {

    private final KraftAdminUsersRepository kraftAdminUsersRepository;
    private final PasswordEncoder kraftPasswordEncoder;

    @Autowired
    public KraftAuthProvider(KraftAdminUsersRepository kraftAdminUsersRepository,
                             @Qualifier("kraftPasswordEncoder") PasswordEncoder kraftPasswordEncoder) {
        this.kraftAdminUsersRepository = kraftAdminUsersRepository;
        this.kraftPasswordEncoder = kraftPasswordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("Using KRAFT provider");
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        AdminUser admin = kraftAdminUsersRepository.findByUsername(username)
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
