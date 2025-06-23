package com.bowerzlabs.events;


import com.bowerzlabs.EntityUtil;
import com.bowerzlabs.dtos.Subject;
import com.bowerzlabs.models.kraftmodels.AdminUserAction;
import com.bowerzlabs.repository.kraftrepos.KraftUserActionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserActionEventListener implements ApplicationListener<UserActionEvent> {
    private final KraftUserActionRepository kraftUserActionRepository;

    public UserActionEventListener(KraftUserActionRepository kraftUserActionRepository) {
        this.kraftUserActionRepository = kraftUserActionRepository;
    }

    /**
     * @param event
     */
    @Async
    @Override
    public void onApplicationEvent(UserActionEvent event) {
        AdminUserAction userAction = new AdminUserAction();
        userAction.setAdminUser(event.getAdminUser());
        userAction.setAction(event.getUserActionType());
//        log.info("id Values {}", event.getEntity().getPrimaryKey());
        userAction.setSubject(event.getSubject());
        kraftUserActionRepository.save(userAction);
    }

    /**
     * @return
     */
    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
