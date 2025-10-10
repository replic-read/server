package com.rere.server.inter.dto.request;

/**
 * Request body with information to create an account.
 */
public record CreateAccountRequest(String email, String password, int profileColor, String username) {
}
