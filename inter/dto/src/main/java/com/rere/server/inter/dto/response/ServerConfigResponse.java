package com.rere.server.inter.dto.response;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body that has information about the server config.
 */
public record ServerConfigResponse(
        @ValidationMetadata(FieldType.CONFIG_CREATE_REPLIC_GROUP) String createReplicGroup,
        @ValidationMetadata(FieldType.CONFIG_ACCESS_REPLIC_GROUP) String accessReplicGroup,
        @ValidationMetadata(FieldType.CONFIG_CREATE_REPORT_GROUP) String createReportGroup,
        @ValidationMetadata(FieldType.CONFIG_MAX_EXP) String maximumExpirationPeriod,
        @ValidationMetadata(FieldType.CONFIG_LIMIT_PERIOD) String replicLimitPeriod,
        @ValidationMetadata(FieldType.CONFIG_LIMIT_COUNT) Integer replicLimitCount,
        @ValidationMetadata(FieldType.CONFIG_LIMIT_START) String replicLimitStart,
        @ValidationMetadata(FieldType.CONFIG_ALLOW_SIGNUP) boolean allowSignup
) {

}
