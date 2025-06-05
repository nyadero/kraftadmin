package com.bowerzlabs.annotations;

import java.lang.annotation.*;

/**
 * Marks a field as searchable when building criteria builder for the entity
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Searchable {}
