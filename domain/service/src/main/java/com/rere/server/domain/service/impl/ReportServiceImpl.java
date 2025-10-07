package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.report.Report;
import com.rere.server.domain.model.report.ReportState;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.repository.ReplicRepository;
import com.rere.server.domain.repository.ReportRepository;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Implementation of the replic service.
 */
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepo;

    private final ReplicRepository replicRepo;

    private final AccountRepository accountRepo;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepo, ReplicRepository replicRepo, AccountRepository accountRepo, ServerConfigService configService) {
        this.reportRepo = reportRepo;
        this.replicRepo = replicRepo;
        this.accountRepo = accountRepo;
    }

    @Nonnull
    @Override
    public List<Report> getReports(Comparator<Report> sort, String query) {
        Stream<Report> stream = reportRepo.getAll()
                .stream()
                .filter(report -> query == null || (report.getDescription() != null && report.getDescription().contains(query)));

        if (sort != null) {
            stream = stream.sorted(sort);
        }

        return stream.toList();
    }

    @Nonnull
    @Override
    public Report reportReplic(@Nonnull UUID replicId, UUID accountId, String description) throws NotFoundException, OperationDisabledException {
        Replic replic = replicRepo
                .getById(replicId)
                .orElseThrow(() -> new NotFoundException(NotFoundSubject.REPLIC, replicId));
        Account account = accountId == null ? null :
                accountRepo
                        .getById(accountId)
                        .orElseThrow(() -> new NotFoundException(NotFoundSubject.ACCOUNT, accountId));

        Report report = new Report(UUID.randomUUID(), Instant.now(), replic, description, account, ReportState.OPEN);
        return reportRepo.save(report);
    }
}
