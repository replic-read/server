package com.rere.server.inter.dto.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or method parameter with information about the expected format (validation) and metadata (used for documentation generation).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidationMetadata {

    /**
     * The type of field or parameter the annotated element is.
     * @return The field type.
     */
    FieldType value() default FieldType.NONE;

    /**
     * Whether the value is required. If this is false, null values are also allowed.
     *
     * @return Whether null values are allowed.
     */
    boolean required() default true;

}
