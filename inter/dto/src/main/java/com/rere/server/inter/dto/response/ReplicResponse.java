package com.rere.server.inter.dto.response;

import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body that contains information about a replic.
 */
public record ReplicResponse(
        @ValidationMetadata(FieldType.REPLIC_ID) String id,
        @ValidationMetadata(FieldType.CREATED_TIMESTAMP) String createdTimestamp,
        @ValidationMetadata(FieldType.REPLIC_DESCRIPTION) String description,
        @ValidationMetadata(FieldType.REPLIC_STATE) String replicState,
        @ValidationMetadata(FieldType.REPLIC_ORIGINAL_URL) String originalUrl,
        @ValidationMetadata(FieldType.REPLIC_SIZE) long size,
        @ValidationMetadata(FieldType.REPLIC_HOST_URL) String hostUrl,
        @ValidationMetadata(FieldType.REPLIC_EXPIRATION) String expiration,
        @ValidationMetadata(FieldType.REPLIC_AUTHOR_ID) String authorId,
        @ValidationMetadata(FieldType.REPLIC_AUTHOR_ID) String mediaMode,
        @ValidationMetadata(FieldType.REPLIC_PASSWORD) boolean hasPassword
) {

}
