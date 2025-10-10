package com.rere.server.inter.execution.dto.response;

/**
 * Response body with an account and auth tokens.
 */
public record AccountWithTokensReponse(AccountResponse account, String accessToken, String refreshToken) {

}
