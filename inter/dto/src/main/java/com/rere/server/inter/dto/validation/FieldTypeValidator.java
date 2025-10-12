package com.rere.server.inter.dto.validation;

import com.rere.server.inter.dto.SerializationUtils;
import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.validation.EnumErrorResponse;
import com.rere.server.inter.dto.error.validation.PatternErrorResponse;
import com.rere.server.inter.dto.error.validation.RequiredErrorResponse;
import com.rere.server.inter.dto.error.validation.SpecificFormatErrorResponse;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Constraint-validator that takes the annotation-metadata provided by {@link ValidationMetadata} annotation and does individual validating.
 */
public class FieldTypeValidator implements ConstraintValidator<ValidationMetadata, Object> {

    /**
     * The metadata annotation.
     */
    private ValidationMetadata metadata;

    /**
     * The field-type from the annotation.
     */
    private FieldType fieldType;

    /**
     * Validates whether an object is a string according to a given regex.
     * @param value The value.
     * @param pattern The regex.
     * @return Whether the object is valid.
     */
    private static boolean validatePattern(Object value, String pattern) {
        return value instanceof String && value.toString().matches(pattern);
    }

    /**
     * Validates whether an object is contained in a list of allowed values.
     * @param value The value.
     * @param values The allowed values.
     * @return Whether the object is valid.
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    private static boolean validateAllowedValues(Object value, String[] values) {
        return Arrays.asList(values).contains(value);
    }

    /**
     * Validates whether an object matches a {@link SpecificFormat}.
     * @param format The format.
     * @param value The value.
     * @return The error response, or null.
     */
    private static ErrorResponseInfo validateSpecificFormat(SpecificFormat format, Object value) {
        return switch (format) {
            case SpecificFormat.URL -> validateUrl(value) ? null : new SpecificFormatErrorResponse(value, format);
            case SpecificFormat.JAVA_PERIOD ->
                    validateJavaPeriod(value) ? null : new SpecificFormatErrorResponse(value, format);
            case SpecificFormat.POSITIVE_INTEGER ->
                    validatePositiveInteger(value) ? null : new SpecificFormatErrorResponse(value, format);
        };
    }

    private static boolean validatePositiveInteger(Object value) {
        try {
            return Integer.parseInt(value.toString()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validateJavaPeriod(Object value) {
        try {
            Period.parse(value.toString());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean validateUrl(Object value) {
        try {
            URI.create(value.toString()).toURL();
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public void initialize(ValidationMetadata constraintAnnotation) {
        this.metadata = constraintAnnotation;
        this.fieldType = this.metadata.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!metadata.required() && value == null) { // Not required means we allow null values.
            return true;
        }

        Set<ErrorResponseInfo> errorInfos = new HashSet<>();

        if (fieldType.getValues() != null) {
            boolean valid = validateAllowedValues(value, fieldType.getValues().get());
            if (!valid) {
                errorInfos.add(new EnumErrorResponse(fieldType.getValues().get(), value));
            }
        }
        if (fieldType.getPattern() != null) {
            boolean valid = validatePattern(value, fieldType.getPattern());
            if (!valid) {
                errorInfos.add(new PatternErrorResponse(fieldType.getPattern(), value));
            }
        }
        if (metadata.required()) {
            boolean valid = value != null;
            if (!valid) {
                errorInfos.add(new RequiredErrorResponse());
            }
        }
        if (fieldType.getSpecificFormat() != null) {
            errorInfos.add(validateSpecificFormat(fieldType.getSpecificFormat(), value));
        }

        if (!errorInfos.isEmpty()) {
            context.disableDefaultConstraintViolation();

            /*
             * Jakarta validation only allows us to pass a custom error message when a validation fails.
             * Because we want to pass object to the validation exception that is thrown, we serialize
             *      the error info object into a string (base 64) and deserialize it when we catch the
             *      exception later on.
             */
            for (ErrorResponseInfo errorInfo : errorInfos) {
                String errorMessage = SerializationUtils.toBase64(errorInfo);
                context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
            }
        }

        return errorInfos.isEmpty();
    }
}
