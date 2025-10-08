package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.domain.model.account.AuthTokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AuthTokenImpl implements AuthToken {

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Builder.Default
    private final Instant creationTimestamp = Instant.now();

    @Builder.Default
    private final UUID token = UUID.randomUUID();

    @Builder.Default
    private final Instant expirationTimestamp = Instant.now();

    @Builder.Default
    private final UUID accountId = UUID.randomUUID();

    @Builder.Default
    private final AuthTokenType type = AuthTokenType.REFRESH_TOKEN;

    @Builder.Default
    private final String data = null;

    @Builder.Default
    private boolean invalidated = false;

}
