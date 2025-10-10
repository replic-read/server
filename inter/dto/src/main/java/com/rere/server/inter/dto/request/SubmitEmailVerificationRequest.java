package com.rere.server.inter.dto.request;

/**
 * Request body with information of an email token.
 */
public record SubmitEmailVerificationRequest(String emailToken) {
}
