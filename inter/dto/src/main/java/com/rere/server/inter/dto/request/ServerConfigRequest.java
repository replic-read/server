package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with information to set the server config.
 */
public record ServerConfigRequest(
        @ValidationMetadata(FieldType.CONFIG_CREATE_REPLIC_GROUP) String createReplicGroup,
        @ValidationMetadata(FieldType.CONFIG_ACCESS_REPLIC_GROUP) String accessReplicGroup,
        @ValidationMetadata(FieldType.CONFIG_CREATE_REPORT_GROUP) String createReportGroup,
        @ValidationMetadata(value = FieldType.CONFIG_MAX_EXP, required = false) String maximumExpirationPeriod,
        @ValidationMetadata(value = FieldType.CONFIG_LIMIT_PERIOD, required = false) String replicLimitPeriod,
        @ValidationMetadata(value = FieldType.CONFIG_LIMIT_COUNT, required = false) Integer replicLimitCount,
        @ValidationMetadata(FieldType.CONFIG_ALLOW_SIGNUP) boolean allowSignup
) {
}
