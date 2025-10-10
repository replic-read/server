package com.rere.server.inter.dto.request;

/**
 * Request body with information to set the server config.
 */
public record ServerConfigRequest(String createReplicGroup, String accessReplicGroup, String createReportGroup,
                                  String maximumExpirationPeriod, String replicLimitPeriod, Integer replicLimitCount,
                                  boolean allowSignup) {
}
