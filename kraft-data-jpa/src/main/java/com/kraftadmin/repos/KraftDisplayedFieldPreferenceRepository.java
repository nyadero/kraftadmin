package com.kraftadmin.repos;

import com.kraftadmin.kraft_jpa_entities.DisplayFieldsPreference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//@ConditionalOnKraftAdminEnabled
@ConditionalOnProperty(name = "kraft.kraft-admin.enabled", havingValue = "JPA")
public interface KraftDisplayedFieldPreferenceRepository extends JpaRepository<DisplayFieldsPreference, String> {
}
