package com.bowerzlabs;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class KraftSecurityConfig {
    public static final String ADMIN_CONTEXT_KEY = "KRAFT_ADMIN_SECURITY_CONTEXT";

    private final KraftAuthProvider kraftAuthProvider;

    public KraftSecurityConfig(KraftAuthProvider kraftAuthProvider) {
        this.kraftAuthProvider = kraftAuthProvider;
    }

    @Bean
    public SessionRegistry kraftSessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Custom Security Context Repository that isolates admin context
     */
    @Bean
    public SecurityContextRepository kraftAdminSecurityContextRepository() {
        return new SecurityContextRepository() {
            private final String ADMIN_CONTEXT_KEY = "KRAFT_ADMIN_SECURITY_CONTEXT";

            @Override
            public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
                HttpServletRequest request = requestResponseHolder.getRequest();
                HttpSession session = request.getSession(false);

                if (session != null) {
                    SecurityContext context = (SecurityContext) session.getAttribute(ADMIN_CONTEXT_KEY);
                    if (context != null) {
                        return context;
                    }
                }

                return SecurityContextHolder.createEmptyContext();
            }

            @Override
            public void saveContext(SecurityContext context, HttpServletRequest request,
                                    HttpServletResponse response) {
                if (context == null || context.getAuthentication() == null) {
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.removeAttribute(ADMIN_CONTEXT_KEY);
                    }
                } else {
                    HttpSession session = request.getSession(true);
                    session.setAttribute(ADMIN_CONTEXT_KEY, context);
                }
            }

            @Override
            public boolean containsContext(HttpServletRequest request) {
                HttpSession session = request.getSession(false);
                return session != null && session.getAttribute(ADMIN_CONTEXT_KEY) != null;
            }
        };
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
                        .invalidateHttpSession(false) // Don't invalidate the whole session
                        .clearAuthentication(true)
                        .addLogoutHandler((request, response, authentication) -> {
                            // Clear only the admin security context
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.removeAttribute(ADMIN_CONTEXT_KEY);
                            }
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(-1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/admin/auth/login?expired")
                        .sessionRegistry(kraftSessionRegistry())
                )
                .securityContext(context -> context
                        .requireExplicitSave(false)
                        .securityContextRepository(kraftAdminSecurityContextRepository())
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/admin/auth/login?expired");
                        })
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Custom filter to ensure admin authentication is properly isolated
     */
    @Component
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public static class AdminSecurityContextFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                             FilterChain chain) throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Only process admin requests
            if (httpRequest.getRequestURI().startsWith("/admin")) {
                // Store the original context
                SecurityContext originalContext = SecurityContextHolder.getContext();

                try {
                    // Create a new empty context for admin requests
                    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
                    chain.doFilter(request, response);
                } finally {
                    // Restore the original context
                    SecurityContextHolder.setContext(originalContext);
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }
}