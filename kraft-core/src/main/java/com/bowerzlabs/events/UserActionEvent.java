package com.bowerzlabs.events;

import com.bowerzlabs.constants.UserActionType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.dtos.Subject;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import org.springframework.context.ApplicationEvent;

public class UserActionEvent extends ApplicationEvent {
    private final UserActionType userActionType;
    private final AdminUser adminUser;
    private final Subject subject;

    public UserActionEvent(Object source, UserActionType userActionType, AdminUser adminUser, Subject subject) {
        super(source);
        this.userActionType = userActionType;
        this.adminUser = adminUser;
        this.subject = subject;
    }

    public UserActionType getUserActionType() {
        return userActionType;
    }

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public Subject getSubject() {
        return subject;
    }
}
