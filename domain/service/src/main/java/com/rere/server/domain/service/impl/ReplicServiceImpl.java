package com.rere.server.domain.service.impl;

import com.rere.server.domain.io.ReplicFileAccessor;
import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.exception.InvalidExpirationException;
import com.rere.server.domain.model.exception.InvalidPasswordException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.domain.model.exception.ReplicContentWriteException;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;
import com.rere.server.domain.model.replic.BaseReplic;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicAccess;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.repository.ReplicAccessRepository;
import com.rere.server.domain.repository.ReplicRepository;
import com.rere.server.domain.service.FileWriterCallback;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Implementation of the replic service.
 */
public class ReplicServiceImpl implements ReplicService {

    private final ReplicRepository replicRepo;

    private final AccountRepository accountRepo;

    private final ReplicAccessRepository accessRepo;

    private final ReplicFileAccessor fileAccessor;

    private final ServerConfigService configService;

    private final PasswordEncoder encoder;

    @Autowired
    public ReplicServiceImpl(ReplicRepository replicRepo, AccountRepository accountRepo, ReplicAccessRepository accessRepo, ReplicFileAccessor fileAccessor, ServerConfigService configService, PasswordEncoder encoder) {
        this.replicRepo = replicRepo;
        this.accountRepo = accountRepo;
        this.accessRepo = accessRepo;
        this.fileAccessor = fileAccessor;
        this.configService = configService;
        this.encoder = encoder;
    }

    private static boolean replicMatchesQuery(Replic replic, String query) {
        return (replic.getDescription() != null && replic.getDescription().contains(query)) ||
               (replic.getOriginalUrl().toString().contains(query));
    }

    @Override
    public Replic createReplic(URL originalUrl, MediaMode mediaMode, String description, Instant expiration, String password, Account account, FileWriterCallback fileWriterCallback) throws ReplicQuotaMetException, ReplicContentWriteException, InvalidExpirationException {
        ServerConfig config = configService.get();
        ReplicLimitConfig limit = config.getLimit();
        checkAccountQuotaLimit(account, limit);

        if (!config.allowsExpiration(LocalDate.now(), expiration, ZoneId.systemDefault())) {
            Instant maximumExpiration = Instant.now().atZone(ZoneId.systemDefault()).plus(config.getMaximumActivePeriod()).toInstant();
            throw new InvalidExpirationException(expiration == null, maximumExpiration, expiration);
        }

        UUID replicId = UUID.randomUUID();
        File replicFile = fileAccessor.createForReplic(replicId);
        if (!fileWriterCallback.write(replicFile)) {
            throw new ReplicContentWriteException();
        }

        long fileSize;
        try {
            fileSize = fileAccessor.getDataSize(replicId);
        } catch (NotFoundException e) {
            throw new IllegalStateException(e);
        }

        BaseReplic baseReplic = new BaseReplic(replicId, Instant.now(), originalUrl, mediaMode,
                ReplicState.ACTIVE, description, expiration, encoder.encode(password), account);
        Replic replic = new Replic(baseReplic, fileSize);
        replic = replicRepo.save(replic);

        return replic;
    }

    private void checkAccountQuotaLimit(Account account, ReplicLimitConfig limit) throws ReplicQuotaMetException {
        if (account != null && limit != null) {
            Instant periodStart = getCurrentPeriodStart(limit);
            long userReplicCount = getCreatedReplicCountForUser(account, periodStart);
            if (userReplicCount == limit.getCount()) {
                throw new ReplicQuotaMetException(account);
            }
        }
    }

    private Instant getCurrentPeriodStart(ReplicLimitConfig limit) {
        if (limit == null) {
            return null;
        }

        Instant periodStart = limit.getPeriodStart();
        while (periodStart.plus(limit.getPeriod()).isBefore(Instant.now())) {
            periodStart = periodStart.plus(limit.getPeriod());
        }

        return periodStart;
    }

    private long getCreatedReplicCountForUser(Account account, Instant start) {
        return replicRepo.getAll().stream()
                .filter(replic -> replic.getOwner() != null && replic.getOwner().getId().equals(account.getId()))
                .filter(access -> access.getCreationTimestamp().isAfter(start))
                .count();
    }

    @Override
    public List<Replic> getReplics(Comparator<Replic> sort, UUID replicId, UUID accountId, Set<ReplicState> stateFilter, String query) {
        Stream<Replic> stream = replicRepo.getAll().stream()
                .filter(replic -> accountId == null || (replic.getOwner() != null && replic.getOwner().getId().equals(accountId)))
                .filter(replic -> replicId == null || replic.getId().equals(replicId))
                .filter(replic -> stateFilter == null || stateFilter.contains(replic.getState()))
                .filter(replic -> query == null || replicMatchesQuery(replic, query));

        if (sort != null) {
            stream = stream.sorted(sort);
        }

        return stream.toList();
    }

    @Override
    public Replic setReplicState(UUID replicId, ReplicState state) throws NotFoundException {
        Replic replic = replicRepo.getAll().stream()
                .filter(r -> r.getId().equals(replicId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(NotFoundSubject.REPLIC, replicId));

        replic.setState(state);
        return replicRepo.save(replic);
    }

    @Override
    public ReplicAccess visitReplic(UUID replicId, UUID visitorId) throws NotFoundException {
        Replic replic = replicRepo.getById(replicId)
                .orElseThrow(() -> new NotFoundException(NotFoundSubject.REPLIC, replicId));

        Account account = visitorId == null ? null :
                accountRepo.getById(visitorId)
                        .orElseThrow(() -> new NotFoundException(NotFoundSubject.ACCOUNT, visitorId));

        ReplicAccess access = new ReplicAccess(UUID.randomUUID(), Instant.now(), replic, account);

        return accessRepo.save(access);
    }

    @Override
    public InputStream receiveContent(UUID replicId, String password) throws NotFoundException, InvalidPasswordException {
        Replic replic = replicRepo
                .getById(replicId)
                .orElseThrow(() -> new NotFoundException(NotFoundSubject.REPLIC, replicId));

        if(replic.requiresPassword()) {
            boolean matches = encoder.matches(password, replic.getPasswordHash());
            if(!matches) {
                throw new InvalidPasswordException();
            }
        }

        return fileAccessor.getDataStream(replicId);
    }
}
