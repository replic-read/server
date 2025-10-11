package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Request body with information to create a report.
 */
public record CreateReportRequest(
        @ValidationMetadata(value = FieldType.REPORT_DESCRIPTION, required = false) String description
) {
}
