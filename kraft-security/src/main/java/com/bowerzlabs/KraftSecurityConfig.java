package com.bowerzlabs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class KraftSecurityConfig {

    private final KraftAuthProvider kraftAuthProvider;

    public KraftSecurityConfig(KraftAuthProvider kraftAuthProvider) {
        this.kraftAuthProvider = kraftAuthProvider;
    }

    @Bean
    public SessionRegistry kraftSessionRegistry() {
        return new SessionRegistryImpl();
    }


    /**
     * This filter chain **only secures /admin/**.
     * All other endpoints remain unaffected, even if parent app has no security.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**") // Limit scope of this config to routes containing 'admin'
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/admin/auth/**",
                                "/admin/uploads/**", "/uploads/**",
                                "/admin/css/**", "/admin/js/**", "/admin/images/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/admin/auth/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll()
                )
                .authenticationProvider(kraftAuthProvider)
                .logout(logout -> logout
                        .logoutUrl("/admin/auth/logout")
                        .logoutSuccessUrl("/admin/auth/login?logout")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
////                        .sessionFixation().changeSessionId()
//                        .maximumSessions(-1)
//                        .maxSessionsPreventsLogin(false)
//                        .expiredUrl("/admin/auth/login?expired")
//                        .sessionRegistry(kraftSessionRegistry())
//                )
//                .securityContext(context -> context
//                        .requireExplicitSave(false)
//                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
//                .rememberMe(rememberMe -> rememberMe
//                        .rememberMeCookieName("KRAFT_ADMIN_REMEMBER_ME")
//                )
//                .requestCache(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/admin/auth/login?expired");
                        })
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
