package com.rere.server.inter.dto.response;

/**
 * Response body with partial information about an account.
 */
public record PartialAccountResponse(String username, int profileColor, String accountState) {

}
