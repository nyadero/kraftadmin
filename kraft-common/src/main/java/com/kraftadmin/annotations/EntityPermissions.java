package com.kraftadmin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityPermissions {
    boolean allowCreate() default true;

    boolean allowEdit() default true;

    boolean allowDelete() default true;

    boolean allowExport() default false;
}
