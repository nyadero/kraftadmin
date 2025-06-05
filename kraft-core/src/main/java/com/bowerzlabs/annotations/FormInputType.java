package com.bowerzlabs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormInputType {
    Type value(); // Define the type of input field

    enum Type {
        TEXT, NUMBER, COLOR, CHECKBOX, IMAGE, DATE, EMAIL, PASSWORD, FILE, TEXTAREA, WYSIWYG, DATETIME, TIME, RANGE, TEL, URL, RADIO
    }
}
