package com.rere.server.inter.dto.response;

/**
 * Response body with full information about an account.
 */
public record AccountResponse(String id, String createdTimestamp, String email, String username, int profileColor,
                              String accountState) {

}
