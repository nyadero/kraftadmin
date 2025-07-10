package com.bowerzlabs.annotations;

import com.bowerzlabs.constants.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to customize how a field is displayed, validated, and used
 * within the KraftAdmin panel forms and tables.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KraftAdminField {

    /**
     * Label to display on forms/tables.
     */
    String label() default "";

    /**
     * Field group/section in the form (e.g., "Personal Info").
     */
    String group() default "";

    /**
     * Type of input (text, number, textarea, select, date, etc).
     */
    FieldType inputType() default FieldType.TEXT;

    /**
     * Whether the field should be shown in the list/table view.
     */
    boolean showInTable() default true;

    /**
     * Whether the field is editable in forms.
     */
    boolean editable() default true;

    /**
     * Whether the field is required in forms.
     */
    boolean required() default false;

    /**
     * Whether the field should be searchable in the admin.
     */
    boolean searchable() default false;

    /**
     * Whether the field should be sortable in the admin.
     */
    boolean sortable() default false;

    /**
     * Whether the field should be filterable in the admin.
     */
    boolean filterable() default false;

    /**
     * Placeholder text for inputs.
     */
    String placeholder() default "";

    /**
     * Readonly field — display but don’t allow changes.
     */
    boolean readonly() default false;

    /**
     * Provide custom display formatter (optional).
     */
    String formatter() default "";
}
