package com.rere.server.domain.service.impl;

import com.rere.server.domain.io.ReplicFileAccessor;
import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.exception.ExpiredException;
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
import com.rere.server.domain.repository.ReplicAccessRepository;
import com.rere.server.domain.repository.ReplicRepository;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.FileWriterCallback;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
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

    private final AccountService accountService;

    private final ReplicAccessRepository accessRepo;

    private final ReplicFileAccessor fileAccessor;

    private final ServerConfigService configService;

    private final PasswordEncoder encoder;

    private final QuotaService quotaService;

    private final Clock clock;

    @Autowired
    public ReplicServiceImpl(ReplicRepository replicRepo, AccountService accountService, ReplicAccessRepository accessRepo, ReplicFileAccessor fileAccessor, ServerConfigService configService, PasswordEncoder encoder, QuotaService quotaService, Clock clock) {
        this.replicRepo = replicRepo;
        this.accountService = accountService;
        this.accessRepo = accessRepo;
        this.fileAccessor = fileAccessor;
        this.configService = configService;
        this.encoder = encoder;
        this.quotaService = quotaService;
        this.clock = clock;
    }

    private static boolean replicMatchesQuery(ReplicBaseData replic, String query) {
        return (replic.getDescription() != null && replic.getDescription().contains(query)) ||
               (replic.getOriginalUrl().toString().contains(query));
    }

    @Override
    public Replic createReplic(URL originalUrl, MediaMode mediaMode, String description, Instant expiration, String password, Account account, FileWriterCallback fileWriterCallback) throws ReplicQuotaMetException, ReplicContentWriteException, InvalidExpirationException {
        if (account != null) {
            quotaService.checkAccountQuota(account.getId());
        }
        ServerConfig config = configService.get();

        if (!config.allowsExpiration(LocalDate.now(clock.getZone()), expiration, clock.getZone())) {
            Instant maximumExpiration = clock.instant().atZone(clock.getZone()).plus(config.getMaximumActivePeriod()).toInstant();
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
                .passwordHash(password != null ? encoder.encode(password) : null)
                .ownerId(account != null ? account.getId() : null)
                .build();

        baseData = replicRepo.saveModel(baseData);

        return ReplicImpl.of(fileData, baseData);
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
                accountService.getAccountById(visitorId)
                        .orElseThrow(() -> NotFoundException.account(visitorId));

        ReplicAccess access = ReplicAccessImpl.builder()
                .replicId(replic.getId())
                .visitorId(account != null ? account.getId() : null)
                .build();

        accessRepo.saveModel(access);
        return access;
    }

    @Override
    public InputStream receiveContent(UUID replicId, String password)
            throws NotFoundException, InvalidPasswordException, ExpiredException {
        Replic replic = getReplicById(replicId)
                .orElseThrow(() -> NotFoundException.replic(replicId));

        if(replic.requiresPassword()) {
            boolean matches = password != null && encoder.matches(password, replic.getPasswordHash());
            if(!matches) {
                throw new InvalidPasswordException();
            }
        }

        if (replic.getExpirationTimestamp() != null &&
            replic.getExpirationTimestamp().isBefore(clock.instant())) {
            throw ExpiredException.replic(replicId);
        }

        return replic.getContentStream();
    }
}
