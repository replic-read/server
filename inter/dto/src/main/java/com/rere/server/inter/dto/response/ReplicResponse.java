package com.rere.server.inter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;

/**
 * Response body that contains information about a replic.
 */
public record ReplicResponse(
        @ValidationMetadata(FieldType.REPLIC_ID) String id,
        @ValidationMetadata(FieldType.CREATED_TIMESTAMP) @JsonProperty("created_timestamp") String createdTimestamp,
        @ValidationMetadata(FieldType.REPLIC_DESCRIPTION) String description,
        @ValidationMetadata(FieldType.REPLIC_STATE) @JsonProperty("replic_state") String replicState,
        @ValidationMetadata(FieldType.REPLIC_ORIGINAL_URL) @JsonProperty("original_url") String originalUrl,
        @ValidationMetadata(FieldType.REPLIC_SIZE) long size,
        @ValidationMetadata(FieldType.REPLIC_HOST_URL) @JsonProperty("host_url") String hostUrl,
        @ValidationMetadata(FieldType.REPLIC_EXPIRATION) String expiration,
        @ValidationMetadata(FieldType.REPLIC_AUTHOR_ID) @JsonProperty("author_id") String authorId,
        @ValidationMetadata(FieldType.REPLIC_AUTHOR_ID) @JsonProperty("media_mode") String mediaMode,
        @ValidationMetadata(FieldType.REPLIC_PASSWORD) @JsonProperty("has_password") boolean hasPassword
) {

}
