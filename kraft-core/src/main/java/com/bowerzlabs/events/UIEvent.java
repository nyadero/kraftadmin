package com.bowerzlabs.events;

import com.bowerzlabs.constants.Status;
import org.springframework.context.ApplicationEvent;

public class UIEvent extends ApplicationEvent {
    private final String message;
    private final Status status;

    public UIEvent(Object source, String message, Status status) {
        super(source);
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Status getStatus() {
        return status;
    }

}
