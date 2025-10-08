package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AccountImpl implements Account {

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Builder.Default
    private final Instant creationTimestamp = Instant.now();

    @Builder.Default
    private String email = "";

    @Builder.Default
    private String username = "";

    @Builder.Default
    private String passwordHash = "";

    @Builder.Default
    private boolean isAdmin = false;

    @Builder.Default
    private AccountState accountState = AccountState.ACTIVE;

    @Builder.Default
    private int profileColor = 0;

}
