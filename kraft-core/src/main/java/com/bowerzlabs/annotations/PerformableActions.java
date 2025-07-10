package com.bowerzlabs.annotations;

import com.bowerzlabs.constants.PerformableAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * define performable actions on an entity
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.TYPE_USE, ElementType.FIELD})
public @interface PerformableActions {
    PerformableAction[] actions() default {PerformableAction.CREATE};
}
