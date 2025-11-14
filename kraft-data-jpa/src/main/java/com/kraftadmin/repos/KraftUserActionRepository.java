package com.kraftadmin.repos;

import com.kraftadmin.kraft_jpa_entities.AdminUserAction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//@ConditionalOnKraftAdminEnabled
@ConditionalOnProperty(name = "kraft.kraft-admin.enabled", havingValue = "JPA")
public interface KraftUserActionRepository extends JpaRepository<AdminUserAction, Long> {
}
