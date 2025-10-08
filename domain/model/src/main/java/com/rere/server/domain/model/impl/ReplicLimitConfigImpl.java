package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.config.ReplicLimitConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.Period;

@Data
@AllArgsConstructor
@Builder
public class ReplicLimitConfigImpl implements ReplicLimitConfig {

    @Builder.Default
    private Period period = Period.ZERO;

    @Builder.Default
    private Instant periodStart = Instant.now();

    @Builder.Default
    private int count = 0;

}
