package com.rere.server.inter.dto.request;

/**
 * Requests body with information to create a replic.
 */
public record CreateReplicRequest(String originalUrl, String mediaMode, String expiration, String description,
                                  String password) {
}
