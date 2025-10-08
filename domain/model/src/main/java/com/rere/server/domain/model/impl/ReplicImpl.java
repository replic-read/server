package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.domain.model.replic.ReplicFileData;
import com.rere.server.domain.model.replic.ReplicState;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Delegate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ReplicImpl implements Replic {

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
    private long size = 0;
    @Builder.Default
    private InputStream contentStream = new ByteArrayInputStream(new byte[0]);
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

    /**
     * Creates a replic from the base- and filedata.
     * @param fileData The file data.
     * @param baseData The base data.
     * @return The replic.
     */
    public static Replic of(ReplicFileData fileData, ReplicBaseData baseData) {
        return new ReplicDelegate(fileData, baseData);
    }

    private record ReplicDelegate(@Delegate ReplicFileData fileData,
                                  @Delegate ReplicBaseData baseData) implements Replic {

    }

}
