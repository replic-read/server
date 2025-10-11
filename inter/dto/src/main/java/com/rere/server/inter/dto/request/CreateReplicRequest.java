package com.rere.server.inter.dto.request;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Requests body with information to create a replic.
 */
public record CreateReplicRequest(
        @ValidationMetadata(FieldType.REPLIC_ORIGINAL_URL) String originalUrl,
        @ValidationMetadata(FieldType.REPLIC_MEDIA_MODE) String mediaMode,
        @ValidationMetadata(value = FieldType.REPLIC_EXPIRATION, required = false) String expiration,
        @ValidationMetadata(value = FieldType.REPLIC_DESCRIPTION, required = false) String description,
        @ValidationMetadata(value = FieldType.ACCOUNT_PASSWORD, required = false) String password
) {
}
