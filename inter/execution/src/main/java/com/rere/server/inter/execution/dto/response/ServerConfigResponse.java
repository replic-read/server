package com.rere.server.inter.execution.dto.response;

/**
 * Response body that has information about the server config.
 */
public record ServerConfigResponse(String createReplicGroup, String accessReplicGroup, String createReportGroup,
                                   String maximumExpirationPeriod, String replicLimitPeriod, Integer replicLimitCount,
                                   String replicLimitStart, boolean allowSignup) {

}
