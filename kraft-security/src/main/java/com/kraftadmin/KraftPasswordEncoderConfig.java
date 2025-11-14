package com.kraftadmin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//@ConditionalOnProperty(
//        name = "kraft.kraft-security.enabled",
//        havingValue = "true"
//)
public class KraftPasswordEncoderConfig {
    @Bean(name = "kraftPasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
