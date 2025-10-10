package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;
import com.rere.server.domain.model.impl.ReplicBaseDataImpl;
import com.rere.server.domain.model.impl.ReplicLimitConfigImpl;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.domain.repository.ReplicRepository;
import com.rere.server.domain.service.BaseDomainServiceTest;
import com.rere.server.domain.service.ServerConfigService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Clock;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class QuotaServiceImplTest extends BaseDomainServiceTest {

    @Mock
    private ReplicRepository replicRepo;

    @Mock
    private ServerConfigService configService;

    @Mock
    private Clock clock;

    @InjectMocks
    private QuotaServiceImpl subject;

    @Test
    void getCurrentPeriodStartThrowsForNoLimit() {
        ServerConfig config = ServerConfigImpl.builder().limit(null).build();
        when(configService.get()).thenReturn(config);

        assertThrows(OperationDisabledException.class,
                () -> subject.getCurrentPeriodStart());
    }

    @Test
    void getCurrentPeriodStartReturnsCorrectStart() throws OperationDisabledException {
        /*
         * Period start was 12 days ago and lasts 5 days, so it looks like:
         * | . . . . | . . . . / . !
         * where '.' is a passed day and '!' is today. '|' is the start of a new period, and '/' is the start of the current period.
         */
        Instant now = Instant.now();
        Instant periodStart = now.minus(12, ChronoUnit.DAYS);
        ReplicLimitConfig limit = ReplicLimitConfigImpl.builder()
                .count(3)
                .periodStart(periodStart)
                .period(Period.of(0, 0, 5)).build();
        ServerConfig config = ServerConfigImpl.builder()
                .limit(limit).build();
        when(configService.get()).thenReturn(config);
        when(clock.instant()).thenReturn(now);

        Instant returned = subject.getCurrentPeriodStart();

        assertEquals(now.minus(2, ChronoUnit.DAYS), returned);
    }

    @Test
    void getCreatedReplicCountInPeriodReturnsCorrectCount() throws DomainException {
        /*
         * Period start was 12 days ago and lasts 5 days, so it looks like:
         * | . . . . | . . . . / . !
         * where '.' is a passed day and '!' is today. '|' is the start of a new period, and '/' is the start of the current period.
         */
        Instant now = Instant.now();
        Instant periodStart = now.minus(12, ChronoUnit.DAYS);
        ReplicLimitConfig limit = ReplicLimitConfigImpl.builder()
                .count(3)
                .periodStart(periodStart)
                .period(Period.of(0, 0, 5)).build();
        ServerConfig config = ServerConfigImpl.builder()
                .limit(limit).build();
        when(configService.get()).thenReturn(config);
        when(clock.instant()).thenReturn(now);

        UUID id = UUID.randomUUID();
        List<ReplicBaseData> replics = new ArrayList<>();
        // Three in the first period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(9, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(10, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(11, ChronoUnit.DAYS)).ownerId(id).build());
        // Two in second period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(3, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(6, ChronoUnit.DAYS)).ownerId(id).build());
        // Seven in current period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());

        when(replicRepo.getAll()).thenReturn(replics);

        assertEquals(7, subject.getCreatedReplicCountInPeriod(id));
    }

    @Test
    void checkAccountQuotaThrowsIfAbove() throws DomainException {
        /*
         * Period start was 12 days ago and lasts 5 days, so it looks like:
         * | . . . . | . . . . / . !
         * where '.' is a passed day and '!' is today. '|' is the start of a new period, and '/' is the start of the current period.
         */
        Instant now = Instant.now();
        Instant periodStart = now.minus(12, ChronoUnit.DAYS);
        ReplicLimitConfig limit = ReplicLimitConfigImpl.builder()
                .count(3)
                .periodStart(periodStart)
                .period(Period.of(0, 0, 5)).build();
        ServerConfig config = ServerConfigImpl.builder()
                .limit(limit).build();
        when(configService.get()).thenReturn(config);
        when(clock.instant()).thenReturn(now);

        UUID id = UUID.randomUUID();
        List<ReplicBaseData> replics = new ArrayList<>();
        // Three in the first period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(9, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(10, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(11, ChronoUnit.DAYS)).ownerId(id).build());
        // Two in second period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(3, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(6, ChronoUnit.DAYS)).ownerId(id).build());
        // Seven in current period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());

        when(replicRepo.getAll()).thenReturn(replics);

        assertEquals(7, subject.getCreatedReplicCountInPeriod(id));
        // 7 > 3
        assertThrows(ReplicQuotaMetException.class, () -> subject.checkAccountQuota(id));
    }

    @Test
    void checkAccountQuotaDoesntThrowIfUnder() throws DomainException {
        /*
         * Period start was 12 days ago and lasts 5 days, so it looks like:
         * | . . . . | . . . . / . !
         * where '.' is a passed day and '!' is today. '|' is the start of a new period, and '/' is the start of the current period.
         */
        Instant now = Instant.now();
        Instant periodStart = now.minus(12, ChronoUnit.DAYS);
        ReplicLimitConfig limit = ReplicLimitConfigImpl.builder()
                .count(8)
                .periodStart(periodStart)
                .period(Period.of(0, 0, 5)).build();
        ServerConfig config = ServerConfigImpl.builder()
                .limit(limit).build();
        when(configService.get()).thenReturn(config);
        when(clock.instant()).thenReturn(now);

        UUID id = UUID.randomUUID();
        List<ReplicBaseData> replics = new ArrayList<>();
        // Three in the first period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(9, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(10, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(11, ChronoUnit.DAYS)).ownerId(id).build());
        // Two in second period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(3, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(6, ChronoUnit.DAYS)).ownerId(id).build());
        // Seven in current period
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(1, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());
        replics.add(ReplicBaseDataImpl.builder().creationTimestamp(now.minus(2, ChronoUnit.DAYS)).ownerId(id).build());

        when(replicRepo.getAll()).thenReturn(replics);

        assertEquals(7, subject.getCreatedReplicCountInPeriod(id));
        // 7 < 8
        assertDoesNotThrow(() -> subject.checkAccountQuota(id));
    }

    @Test
    void checkAccountQuotaDoesntThrowIfoConfigSetup() {
        ServerConfig config = ServerConfigImpl.builder()
                .limit(null).build();
        when(configService.get()).thenReturn(config);
        UUID id = UUID.randomUUID();

        assertDoesNotThrow(() -> subject.checkAccountQuota(id));
    }

}
