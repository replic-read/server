package com.rere.server.domain.model.impl;

import com.rere.server.domain.model.report.Report;
import com.rere.server.domain.model.report.ReportState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ReportImpl implements Report {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Builder.Default
    private Instant creationTimestamp = Instant.now();

    @Builder.Default
    private UUID replicId = UUID.randomUUID();

    @Builder.Default
    private UUID authorId = null;

    @Builder.Default
    private String description = null;

    @Builder.Default
    private ReportState state = ReportState.OPEN;

}
