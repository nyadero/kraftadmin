package com.bowerzlabs.repository.kraftrepos;

import com.bowerzlabs.models.kraftmodels.AdminUserAction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KraftUserActionRepository extends JpaRepository<AdminUserAction, Long> {
}
