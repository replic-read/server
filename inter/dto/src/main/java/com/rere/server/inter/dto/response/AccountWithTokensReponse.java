package com.rere.server.inter.dto.response;

/**
 * Response body with an account and auth tokens.
 */
public record AccountWithTokensReponse(AccountResponse account, String accessToken, String refreshToken) {

}
