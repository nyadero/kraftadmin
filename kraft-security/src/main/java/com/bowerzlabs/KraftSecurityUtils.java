package com.bowerzlabs;

import com.bowerzlabs.models.kraftmodels.AdminUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ConditionalOnProperty(
        name = "kraft.kraft-security.enabled",
        havingValue = "true"
)
public class KraftSecurityUtils {
    public static AdminUser getLoggedInAdminUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof AdminUser adminUser) {
            return adminUser;
        }

        return null;
    }
}
