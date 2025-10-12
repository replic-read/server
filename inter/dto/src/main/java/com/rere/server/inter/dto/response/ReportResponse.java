package com.rere.server.inter.dto.response;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body that has information about a report.
 */
public record ReportResponse(
        @ValidationMetadata(FieldType.REPORT_ID) String id,
        @ValidationMetadata(FieldType.CREATED_TIMESTAMP) String createdTimestamp,
        @ValidationMetadata(FieldType.REPORT_ACCOUNT_ID) String userId,
        @ValidationMetadata(FieldType.REPORT_REPLIC_ID) String replicId
) {

}
