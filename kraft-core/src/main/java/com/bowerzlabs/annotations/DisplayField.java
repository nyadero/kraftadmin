package com.bowerzlabs.annotations;

import java.lang.annotation.*;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DisplayField {
    /**
    * Field  to display in the relationship
     */
    String value();
}
