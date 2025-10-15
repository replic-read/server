package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.OperationDisabledOperation;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;
import com.rere.server.domain.repository.ReplicRepository;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Implementation of the quota-service.
 */
@Component
public class QuotaServiceImpl implements QuotaService {

    private final ReplicRepository replicRepo;

    private final ServerConfigService configService;

    private final Clock clock;

    @Autowired
    public QuotaServiceImpl(ReplicRepository replicRepo, ServerConfigService configService, Clock clock) {
        this.replicRepo = replicRepo;
        this.configService = configService;
        this.clock = clock;
    }

    @Override
    public Instant getCurrentPeriodStart() throws OperationDisabledException {
        ReplicLimitConfig limit = configService.get().getLimit();
        if (limit == null) {
            throw new OperationDisabledException(OperationDisabledOperation.QUOTA_INFO);
        }

        Instant periodStart = limit.getPeriodStart();
        while (periodStart.plus(limit.getPeriod().getDays(), ChronoUnit.DAYS).isBefore(clock.instant())) {
            periodStart = periodStart.plus(limit.getPeriod());
        }

        return periodStart;
    }

    @Override
    public long getCreatedReplicCountInPeriod(UUID accountId) throws OperationDisabledException {
        Instant periodStart = getCurrentPeriodStart();
        return replicRepo.getAll().stream()
                .filter(replic -> replic.getOwnerId() != null && replic.getOwnerId().equals(accountId))
                .filter(access -> access.getCreationTimestamp().isAfter(periodStart) || access.getCreationTimestamp().equals(periodStart))
                .count();
    }

    @Override
    public void checkAccountQuota(UUID accountId) throws ReplicQuotaMetException {
        try {
            long createdCount = getCreatedReplicCountInPeriod(accountId);
            ReplicLimitConfig limit = configService.get().getLimit();
            if (createdCount >= limit.getCount()) {
                throw new ReplicQuotaMetException(accountId);
            }
        } catch (OperationDisabledException e) {
            // We don't throw any exception.
        }
    }
}
