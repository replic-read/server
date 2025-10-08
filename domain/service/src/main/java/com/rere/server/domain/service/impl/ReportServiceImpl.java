package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.impl.ReportImpl;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.report.Report;
import com.rere.server.domain.model.report.ReportState;
import com.rere.server.domain.repository.ReportRepository;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Implementation of the replic service.
 */
@Component
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepo;

    private final ReplicService replicService;

    private final AccountService accountService;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepo, AccountService accountService, ReplicService replicService) {
        this.reportRepo = reportRepo;
        this.accountService = accountService;
        this.replicService = replicService;
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
    public Report reportReplic(@Nonnull UUID replicId, UUID accountId, String description) throws NotFoundException {
        Replic replic = replicService.getReplicById(replicId)
                .orElseThrow(() -> NotFoundException.replic(replicId));
        Account account = accountId == null ? null :
                accountService
                        .getAccountById(accountId)
                        .orElseThrow(() -> NotFoundException.account(accountId));

        Report report = ReportImpl.builder()
                .replicId(replic.getId())
                .authorId(account != null ? account.getId() : null)
                .description(description)
                .state(ReportState.OPEN).build();

        return reportRepo.save(report);
    }
}
