package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.domain.model.replic.ReplicState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ReplicBaseDataImpl implements ReplicBaseData {

    private static final URL initialUrl;

    static {
        try {
            initialUrl = URI.create("https://example.com/").toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Builder.Default
    private final UUID id = UUID.randomUUID();
    @Builder.Default
    private final Instant creationTimestamp = Instant.now();
    @Builder.Default
    private final URL originalUrl = initialUrl;
    @Builder.Default
    private final MediaMode mediaMode = MediaMode.ALL;
    @Builder.Default
    private ReplicState state = ReplicState.ACTIVE;
    @Builder.Default
    private String description = null;
    @Builder.Default
    private Instant expirationTimestamp = null;
    @Builder.Default
    private String passwordHash = null;
    @Builder.Default
    private UUID ownerId = null;

}
