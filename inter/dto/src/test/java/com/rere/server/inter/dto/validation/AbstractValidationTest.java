package com.rere.server.inter.dto.validation;

import com.rere.server.inter.dto.SerializationUtils;
import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.HttpErrorResponseException;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract test for testing the validation of a specific DTO.
 * @param <D> The dto.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractValidationTest<D> {

    private Validator validator;
    private ValidatorFactory factory;

    /**
     * Gets an array of valid DTOs.
     * @return The valid DTOs.
     */
    protected abstract D[] getValidDtos();

    /**
     * Gets an array of valid DTOs.
     * @return The valid DTOs.
     */
    protected abstract Pair<D, ? extends ErrorResponseInfo>[] getInvalidDtos();

    @BeforeAll
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    void tearDown() {
        factory.close();
    }

    @Test
    void validDtosDontCreateErrors() {
        for (D dto : getValidDtos()) {
            Set<ErrorResponseInfo> errorInfos = validator.validate(dto)
                    .stream().map(violation -> SerializationUtils.<ErrorResponseInfo>fromBase64(violation.getMessageTemplate()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            assertEquals(0, errorInfos.size(),
                    () -> "Expected to find no constraint violation for dto " + dto + ", but got: " + errorInfos + ".");
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void invalidDtosDoCreateErrors() {
        for (Pair<D, ? extends ErrorResponseInfo> pair : getInvalidDtos()) {
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(pair.first()));
            assertInstanceOf(HttpErrorResponseException.class, ex.getCause());
            HttpErrorResponseException e = (HttpErrorResponseException) ex.getCause();

            assertEquals(e.getError(), pair.second());
        }
    }

}