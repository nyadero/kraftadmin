package com.bowerzlabs.repository.kraftrepos;

import com.bowerzlabs.constants.Role;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
//@ConditionalOnProperty(name = "kraft.kraft-jpa.enabled", havingValue = "true")
public interface KraftAdminUsersRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
    Optional<AdminUser> findByRole(Role role);
}
