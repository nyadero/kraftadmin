package com.kraftadmin.events;

import com.kraftadmin.constants.UserActionType;
import com.kraftadmin.kraft_jpa_entities.AdminUser;
import com.kraftadmin.kraft_jpa_entities.Subject;
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
