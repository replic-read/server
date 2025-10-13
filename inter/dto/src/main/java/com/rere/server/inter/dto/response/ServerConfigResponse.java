package com.rere.server.inter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body that has information about the server config.
 */
public record ServerConfigResponse(
        @ValidationMetadata(FieldType.CONFIG_CREATE_REPLIC_GROUP) @JsonProperty("create_replic_group") String createReplicGroup,
        @ValidationMetadata(FieldType.CONFIG_ACCESS_REPLIC_GROUP) @JsonProperty("access_replic_group") String accessReplicGroup,
        @ValidationMetadata(FieldType.CONFIG_CREATE_REPORT_GROUP) @JsonProperty("create_report_group") String createReportGroup,
        @ValidationMetadata(FieldType.CONFIG_MAX_EXP) @JsonProperty("maximum_expiration_period") String maximumExpirationPeriod,
        @ValidationMetadata(FieldType.CONFIG_LIMIT_PERIOD) @JsonProperty("replic_limit_period") String replicLimitPeriod,
        @ValidationMetadata(FieldType.CONFIG_LIMIT_COUNT) @JsonProperty("replic_limit_count") Integer replicLimitCount,
        @ValidationMetadata(FieldType.CONFIG_LIMIT_START) @JsonProperty("replic_limit_start") String replicLimitStart,
        @ValidationMetadata(FieldType.CONFIG_ALLOW_SIGNUP) @JsonProperty("allow_signup") boolean allowSignup
) {

}
