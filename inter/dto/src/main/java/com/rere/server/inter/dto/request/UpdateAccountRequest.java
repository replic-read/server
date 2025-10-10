package com.rere.server.inter.dto.request;

/**
 * Request body with information to update an account.
 */
public record UpdateAccountRequest(String email, String username, int profileColor) {

}
