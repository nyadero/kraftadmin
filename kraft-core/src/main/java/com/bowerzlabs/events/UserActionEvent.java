package com.bowerzlabs.events;

import com.bowerzlabs.constants.UserActionType;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import org.springframework.context.ApplicationEvent;

public class UserActionEvent extends ApplicationEvent {
    private final UserActionType userActionType;
    private final AdminUser adminUser;
    private final String entity;

    public UserActionEvent(Object source, UserActionType userActionType, AdminUser adminUser, String entity) {
        super(source);
        this.userActionType = userActionType;
        this.adminUser = adminUser;
        this.entity = entity;
    }

    public UserActionType getUserActionType() {
        return userActionType;
    }

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public String getEntity() {
        return entity;
    }


}
