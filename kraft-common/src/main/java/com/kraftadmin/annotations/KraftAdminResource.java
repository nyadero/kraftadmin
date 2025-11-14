package com.kraftadmin.annotations;

import com.kraftadmin.constants.PerformableAction;
import com.kraftadmin.constants.Role;

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
    /**
     * Display name of the resource in the admin panel.
     */
    String name() default "";

    /**
     * Group/section in the sidebar or UI for better organization (e.g., "Inventory", "Users").
     */
    String group() default "";

    /**
     * Optional icon to use in the sidebar (unicode or icon name).
     */
    String icon() default "\uD83D\uDCDA"; // default: books emoji

    /**
     * Whether this resource is editable (i.e., CREATE/UPDATE forms).
     */
    boolean editable() default true;

    /**
     * Whether to exclude this resource from the admin UI.
     */
    boolean hidden() default false;

    /**
     * Whether to enable search globally for this entity.
     */
    boolean searchable() default false;

    /**
     * Optional allowed kraft-actions-logs (CRUD or partial)
     */
    PerformableAction[] actions() default {
            PerformableAction.CREATE,
            PerformableAction.READ,
            PerformableAction.UPDATE,
            PerformableAction.DELETE
    };

    /**
     * Whether this resource should be excluded from all KraftAdmin features (same as @NotManageable).
     */
    boolean manageable() default true;

    /**
     * Limit access to only users with SUPER_ADMIN role.
     */
    boolean adminOnly() default false;

    /**
     * Limit access to users with specific roles (same as @RolesAllowed).
     */
    Role[] rolesAllowed() default {
            Role.ADMIN, Role.SUPER_ADMIN, Role.MODERATOR, Role.VIEWER
    };
}