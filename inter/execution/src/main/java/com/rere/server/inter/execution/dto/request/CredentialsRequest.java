package com.rere.server.inter.execution.dto.request;

/**
 * Request body with credentials.
 */
public record CredentialsRequest(String email, String username, String password) {
}
