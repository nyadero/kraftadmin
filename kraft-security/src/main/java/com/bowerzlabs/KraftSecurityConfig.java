//package com.bowerzlabs;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//@Order(1)
//public class KraftSecurityConfig {
//
//    private final KraftAuthProvider kraftAuthProvider;
//
//    public KraftSecurityConfig(KraftAuthProvider kraftAuthProvider) {
//        this.kraftAuthProvider = kraftAuthProvider;
//    }
//
//    @Bean
//    @Order(1)
//    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/admin/**")
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/toast-events").permitAll()
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
//                        .requestMatchers("/admin/auth/**", "/admin/test/**", "/admin/css/**", "/admin/js/**").permitAll()
//                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER", "SUPER_ADMIN")
//                )
//                .formLogin(login -> login
//                        .loginPage("/admin/auth/login")
//                        .loginProcessingUrl("/admin/auth/login")
//                        .defaultSuccessUrl("/admin/dashboard", true)
//                        .failureUrl("/admin/auth/login?error=true")
//                        .permitAll()
//                )
//                .authenticationProvider(kraftAuthProvider)
/// /                .authenticationManager(new ProviderManager(List.of(kraftAuthProvider)))
//                .logout(logout -> logout
//                        .logoutUrl("/admin/auth/logout")
//                        .logoutSuccessUrl("/admin/auth/login?logout")
//                        .permitAll()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                        .sessionFixation().changeSessionId()
//                        .maximumSessions(1)
//                        .expiredUrl("/admin/auth/login?expired")
//                )
//                .csrf(AbstractHttpConfigurer::disable);
//        return http.build();
//    }
//
//
//
//}
//


package com.bowerzlabs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class KraftSecurityConfig {

    private final KraftAuthProvider kraftAuthProvider;

    public KraftSecurityConfig(KraftAuthProvider kraftAuthProvider) {
        this.kraftAuthProvider = kraftAuthProvider;
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
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }


}
