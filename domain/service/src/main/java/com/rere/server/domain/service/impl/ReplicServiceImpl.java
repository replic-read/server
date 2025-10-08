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
import com.rere.server.domain.model.impl.ReplicAccessImpl;
import com.rere.server.domain.model.impl.ReplicBaseDataImpl;
import com.rere.server.domain.model.impl.ReplicImpl;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicAccess;
import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.domain.model.replic.ReplicFileData;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.repository.ReplicAccessRepository;
import com.rere.server.domain.repository.ReplicRepository;
import com.rere.server.domain.service.FileWriterCallback;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Implementation of the replic service.
 */
@Component
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

    private static boolean replicMatchesQuery(ReplicBaseData replic, String query) {
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

        ReplicFileData fileData;
        try {
            fileData = fileAccessor.getFileData(replicId);
        } catch (NotFoundException e) {
            throw new IllegalStateException(e);
        }

        ReplicBaseData baseData = ReplicBaseDataImpl.builder()
                .id(replicId)
                .originalUrl(originalUrl)
                .mediaMode(mediaMode)
                .state(ReplicState.ACTIVE)
                .description(description)
                .expirationTimestamp(expiration)
                .passwordHash(encoder.encode(password))
                .ownerId(account != null ? account.getId() : null)
                .build();

        baseData = replicRepo.saveModel(baseData);

        return ReplicImpl.of(fileData, baseData);
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
                .filter(replic -> replic.getOwnerId() != null && replic.getOwnerId().equals(account.getId()))
                .filter(access -> access.getCreationTimestamp().isAfter(start))
                .count();
    }

    private Replic populateBaseData(ReplicBaseData baseData) {
        ReplicFileData fileData;
        try {
            fileData = fileAccessor.getFileData(baseData.getId());
        } catch (NotFoundException e) {
            throw new IllegalStateException(e);
        }
        return ReplicImpl.of(fileData, baseData);
    }

    @Override
    public List<Replic> getReplics(Comparator<Replic> sort, UUID replicId, UUID accountId, Set<ReplicState> stateFilter, String query) {
        Stream<Replic> stream = replicRepo.getAll().stream()
                .filter(replic -> accountId == null || (replic.getOwnerId() != null && replic.getOwnerId().equals(accountId)))
                .filter(replic -> replicId == null || replic.getId().equals(replicId))
                .filter(replic -> stateFilter == null || stateFilter.contains(replic.getState()))
                .filter(replic -> query == null || replicMatchesQuery(replic, query))
                .map(this::populateBaseData);

        if (sort != null) {
            stream = stream.sorted(sort);
        }

        return stream.toList();
    }

    @Override
    public Optional<Replic> getReplicById(UUID id) {
        return replicRepo.getById(id)
                .map(this::populateBaseData);
    }

    @Override
    public Replic setReplicState(UUID replicId, ReplicState state) throws NotFoundException {
        Replic replic = getReplicById(replicId)
                .orElseThrow(() -> NotFoundException.replic(replicId));

        replic.setState(state);
        replicRepo.saveModel(replic);
        return replic;
    }

    @Override
    public ReplicAccess visitReplic(UUID replicId, UUID visitorId) throws NotFoundException {
        Replic replic = getReplicById(replicId)
                .orElseThrow(() -> new NotFoundException(NotFoundSubject.REPLIC, replicId));

        Account account = visitorId == null ? null :
                accountRepo.getById(visitorId)
                        .orElseThrow(() -> NotFoundException.account(visitorId));

        ReplicAccess access = ReplicAccessImpl.builder()
                .replicId(replic.getId())
                .visitorId(account != null ? account.getId() : null)
                .build();

        accessRepo.saveModel(access);
        return access;
    }

    @Override
    public InputStream receiveContent(UUID replicId, String password) throws NotFoundException, InvalidPasswordException {
        Replic replic = getReplicById(replicId)
                .orElseThrow(() -> NotFoundException.replic(replicId));

        if(replic.requiresPassword()) {
            boolean matches = encoder.matches(password, replic.getPasswordHash());
            if(!matches) {
                throw new InvalidPasswordException();
            }
        }

        return replic.getContentStream();
    }
}
