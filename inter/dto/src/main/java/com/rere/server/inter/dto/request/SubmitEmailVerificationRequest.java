package com.rere.server.inter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with information of an email token.
 */
public record SubmitEmailVerificationRequest(
        @ValidationMetadata(value = FieldType.EMAIL_TOKEN, doValidate = false) @JsonProperty("email_token") String emailToken
) {
}
