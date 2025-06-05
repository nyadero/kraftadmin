package com.bowerzlabs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows user to customize the entity's display on the ui
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.TYPE_USE, ElementType.FIELD})
public @interface KraftAdminResource {
    String name() default "";   // Optional name for the field
    String group() default "";  // Optional group for organization
    String icon() default "\uD83D\uDCDA"; // the resource's icon
    boolean editable() default true;  // User can change name
}



