package com.rere.server.inter.dto.validation;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.HttpErrorResponseException;
import com.rere.server.inter.dto.error.validation.EnumErrorResponse;
import com.rere.server.inter.dto.error.validation.PatternErrorResponse;
import com.rere.server.inter.dto.error.validation.RequiredErrorResponse;
import com.rere.server.inter.dto.error.validation.SpecificFormatErrorResponse;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Constraint-validator that takes the annotation-metadata provided by {@link ValidationMetadata} annotation and does individual validating.
 */
@Slf4j
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
        if (value instanceof Iterable<?>) {
            for (Object o : (Iterable<?>) value) {
                boolean valid = validateAllowedValues(o, values);
                if (!valid) {
                    return false;
                }
            }
            return true;
        }
        return Arrays.asList(values).contains(value);
    }

    /**
     * Validates whether an object matches a {@link SpecificFormat}.
     * @param format The format.
     * @param value The value.
     * @return The error response, or null.
     */
    private static ErrorResponseInfo validateSpecificFormat(SpecificFormat format, Serializable value) {
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
        } catch (MalformedURLException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void initialize(ValidationMetadata constraintAnnotation) {
        this.metadata = constraintAnnotation;
        this.fieldType = this.metadata.value();
    }

    @Override
    @SuppressWarnings("squid:S3516")
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if ((!metadata.required() && object == null) || !metadata.doValidate()) { // Not required means we allow null values.
            return true;
        }

        if (!(object instanceof Serializable || object == null)) {
            throw new IllegalArgumentException("FieldTypeValidator can only validate serializable fields.");
        }
        Serializable value = (Serializable) object;

        Optional<ErrorResponseInfo> infoToThrow = validate(value)
                .stream()
                .findFirst();

        /*
         * Although we extend the ConstraintValidator from jakarta validation, we don't properly use jakarta validation.
         * We use the constraint annotation system to run this code at the same time jakarta validation would normally
         *      run to write our own validation code.
         * Instead of writing constraint violations to the validator context, we throw our own runtime exception that we later catch.
         * This is a hack, but is the only simple way for easy request validation.
         */
        if (infoToThrow.isPresent()) {
            throw new HttpErrorResponseException(infoToThrow.get(), 400);
        }

        return true;
    }

    private Set<ErrorResponseInfo> validate(Serializable value) {
        Set<ErrorResponseInfo> errorInfos = new HashSet<>();

        if (metadata.required()) {
            boolean valid = value != null;
            if (!valid) {
                errorInfos.add(new RequiredErrorResponse());

                // We return, so that for missing values we don't run all other validations.
                return errorInfos;
            }
        }
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
        if (fieldType.getSpecificFormat() != null) {
            ErrorResponseInfo specificFormatError = validateSpecificFormat(fieldType.getSpecificFormat(), value);
            if (specificFormatError != null) {
                errorInfos.add(specificFormatError);
            }
        }

        return errorInfos;
    }
}
