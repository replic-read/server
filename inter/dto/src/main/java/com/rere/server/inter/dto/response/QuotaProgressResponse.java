package com.rere.server.inter.dto.response;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body with information about the quota progress of a user.
 */
public record QuotaProgressResponse(
        @ValidationMetadata(FieldType.QUOTA_PROGRESS) long count
) {

}
