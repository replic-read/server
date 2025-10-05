package com.rere.server.domain.model.report;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.replic.Replic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Report {

    /**
     * The id of the report.
     */
    @NonNull
    private final UUID id;

    /**
     * The creation timestamp of the report.
     */
    @NonNull
    private final Instant creationTimestamp;

    /**
     * The report for which the report was created.
     */
    @NonNull
    private final Replic replic;

    /**
     * The account the report was created by, if it exists.
     */
    private final Account author;

    /**
     * The state the report currently is in.
     */
    @NonNull
    private ReportState state;

}
