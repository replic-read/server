package com.rere.server.inter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body that has information about a report.
 */
public record ReportResponse(
        @ValidationMetadata(FieldType.REPORT_ID) String id,
        @ValidationMetadata(FieldType.CREATED_TIMESTAMP) @JsonProperty("created_timestamp") String createdTimestamp,
        @ValidationMetadata(FieldType.REPORT_ACCOUNT_ID) @JsonProperty("user_id") String userId,
        @ValidationMetadata(FieldType.REPORT_REPLIC_ID) @JsonProperty("replic_id") String replicId
) {

}
