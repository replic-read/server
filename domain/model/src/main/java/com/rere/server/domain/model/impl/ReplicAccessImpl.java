package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.replic.ReplicAccess;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ReplicAccessImpl implements ReplicAccess {

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Builder.Default
    private final Instant creationTimestamp = Instant.now();

    private final UUID replicId;

    @Builder.Default
    private final UUID visitorId = null;

}
