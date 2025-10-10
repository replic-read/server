package com.rere.server.inter.dto.response;

/**
 * Response body that contains information about a replic.
 */
public record ReplicResponse(String id, String createdTimestamp, String description, String replicState,
                             String originalUrl, long size, String hostUrl, String expiration, String authorId,
                             String mediaMode, boolean hasPassword) {

}
