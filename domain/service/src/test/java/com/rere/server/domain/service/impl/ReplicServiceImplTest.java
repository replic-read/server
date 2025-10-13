package com.rere.server.domain.service.impl;

import com.rere.server.domain.io.ReplicFileAccessor;
import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.InvalidExpirationException;
import com.rere.server.domain.model.exception.InvalidPasswordException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.domain.model.exception.ReplicContentWriteException;
import com.rere.server.domain.model.exception.ReplicQuotaMetException;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.model.impl.ReplicBaseDataImpl;
import com.rere.server.domain.model.impl.ReplicFileDataImpl;
import com.rere.server.domain.model.impl.ReplicImpl;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicAccess;
import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.domain.model.replic.ReplicFileData;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.repository.ReplicAccessRepository;
import com.rere.server.domain.repository.ReplicRepository;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.BaseDomainServiceTest;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ServerConfigService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Clock;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link ReplicServiceImpl} class.
 */
class ReplicServiceImplTest extends BaseDomainServiceTest {

    private static final URL URL;

    static {
        try {
            URL = URI.create("https://google.com/").toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Mock
    private ReplicRepository replicRepo;
    @Mock
    private AccountService accountService;
    @Mock
    private ReplicAccessRepository accessRepo;
    @Mock
    private ReplicFileAccessor fileAccessor;
    @Mock
    private ServerConfigService configService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private QuotaService quotaService;
    @Spy
    private Clock clock = Clock.systemUTC();
    @InjectMocks
    private ReplicServiceImpl subject;

    @Test
    void createReplicThrowsForQuotaViolation() throws DomainException {
        Account account = AccountImpl.builder().build();
        doThrow(new ReplicQuotaMetException(account.getId())).when(quotaService).checkAccountQuota(account.getId());

        ReplicQuotaMetException ex = Assertions.assertThrows(ReplicQuotaMetException.class, () -> subject.createReplic(URL, MediaMode.ALL, null, null, null, account, file -> false));
        Assertions.assertEquals(account.getId(), ex.getAccountId());
    }

    @Test
    void createReplicThrowsForMissingExpiration() {
        ServerConfig config = ServerConfigImpl.builder()
                .maximumActivePeriod(Period.of(1, 2, 3)).build();
        when(configService.get()).thenReturn(config);

        InvalidExpirationException ex = Assertions.assertThrows(InvalidExpirationException.class,
                () -> subject.createReplic(URL, MediaMode.ALL, null, null, null, null, file -> false));
        Assertions.assertTrue(ex.isExpirationMissing());
    }

    @Test
    void createReplicThrowsForInvalidExpiration() {
        Instant now = Instant.now();
        ServerConfig config = ServerConfigImpl.builder()
                .maximumActivePeriod(Period.of(0, 0, 5)).build();
        when(configService.get()).thenReturn(config);

        // Attempt to create replic that expires 6 days in the future, but 5 is maximum allowed
        InvalidExpirationException ex = Assertions.assertThrows(InvalidExpirationException.class,
                () -> subject.createReplic(URL, MediaMode.ALL, null, now.plus(6, ChronoUnit.DAYS), null, null, file -> false));
        Assertions.assertFalse(ex.isExpirationMissing());
    }

    @Test
    void createReplicUsesFileCallbackAndThrowsForFailure() {
        ServerConfig config = ServerConfigImpl.builder().build();
        when(configService.get()).thenReturn(config);

        // Attempt to create replic that expires 6 days in the future, but 5 is maximum allowed
        AtomicBoolean wasInsideAccessor = new AtomicBoolean(false);
        Assertions.assertThrows(ReplicContentWriteException.class,
                () -> subject.createReplic(URL, MediaMode.ALL, null, null,
                        null, null, file -> {
                            wasInsideAccessor.set(true);
                            return false;
                        }));
        Assertions.assertTrue(wasInsideAccessor.get());
    }

    @Test
    void createReplicSavesReplicWithCorrectPropertiesAndReturnsIt() throws DomainException {
        ServerConfig config = ServerConfigImpl.builder().build();
        when(configService.get()).thenReturn(config);

        // Attempt to create replic that expires 6 days in the future, but 5 is maximum allowed
        when(fileAccessor.getFileData(any())).thenReturn(ReplicFileDataImpl.builder()
                .size(42).build());
        when(replicRepo.saveModel(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(any())).thenReturn("hash");

        Replic createdReplic = subject.createReplic(URL, MediaMode.ALL, null, null, null, null, file -> true);

        ArgumentCaptor<ReplicBaseData> replicCaptor = ArgumentCaptor.captor();
        verify(replicRepo, times(1)).saveModel(replicCaptor.capture());

        Assertions.assertEquals(createdReplic.getId(), replicCaptor.getValue().getId());
        Assertions.assertEquals(URL, replicCaptor.getValue().getOriginalUrl());
    }

    @Test
    void getReplicsFiltersAccountId() {
        Account acc1 = AccountImpl.builder().build();
        ReplicBaseData replic1 = ReplicBaseDataImpl.builder()
                .ownerId(acc1.getId()).build();
        Account acc2 = AccountImpl.builder().build();
        ReplicBaseDataImpl replic2 = ReplicBaseDataImpl.builder()
                .ownerId(acc2.getId()).build();
        List<ReplicBaseData> replics = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            replics.add(replic1);
        }
        for (int i = 0; i < 5; i++) {
            replics.add(replic2);
        }
        Collections.shuffle(replics);

        when(replicRepo.getAll()).thenReturn(replics);

        List<Replic> filtered = subject.getReplics(null, null, acc1.getId(), null, null);

        Assertions.assertEquals(5, filtered.size());
        for (Replic replic : filtered) {
            Assertions.assertEquals(acc1.getId(), replic.getOwnerId());
        }
    }

    @Test
    void getReplicsFiltersReplicId() {
        UUID specialId = UUID.randomUUID();
        List<ReplicBaseData> replics = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            replics.add(ReplicImpl.builder().build());
        }
        ReplicBaseData specialBaseData = ReplicBaseDataImpl.builder()
                .id(specialId).build();
        replics.add(specialBaseData);
        Collections.shuffle(replics);

        when(replicRepo.getAll()).thenReturn(replics);

        List<Replic> filtered = subject.getReplics(null, specialId, null, null, null);

        Assertions.assertEquals(1, filtered.size());
        Assertions.assertEquals(specialId, filtered.getFirst().getId());
    }

    @Test
    void getReplicsFiltersReplicState() {
        List<ReplicBaseData> replics = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            replics.add(ReplicBaseDataImpl.builder()
                    .state(ReplicState.ACTIVE).build());
            replics.add(ReplicBaseDataImpl.builder()
                    .state(ReplicState.INACTIVE).build());
            replics.add(ReplicBaseDataImpl.builder()
                    .state(ReplicState.REMOVED).build());
        }
        Collections.shuffle(replics);

        when(replicRepo.getAll()).thenReturn(replics);

        List<Replic> filtered1 = subject.getReplics(null, null, null, Set.of(ReplicState.ACTIVE, ReplicState.REMOVED), null);
        List<Replic> filtered2 = subject.getReplics(null, null, null, Set.of(), null);

        Assertions.assertEquals(10, filtered1.size());
        for (Replic replic : filtered1) {
            Assertions.assertTrue(Set.of(ReplicState.ACTIVE, ReplicState.REMOVED).contains(replic.getState()));
        }
        Assertions.assertEquals(0, filtered2.size());
    }

    @Test
    void getReplicsFiltersQuery() throws MalformedURLException {
        List<ReplicBaseData> replics = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            replics.add(ReplicBaseDataImpl.builder().build());
        }
        ReplicBaseData withDescription = ReplicBaseDataImpl.builder()
                .description("adsnksdn" + "the-universe-life-and-everything" + "sdfdsf").build();
        URL specialUrl = URI.create("https://the-universe-life-and-everything.com/").toURL();
        ReplicBaseData withUrl = ReplicBaseDataImpl.builder()
                .originalUrl(specialUrl).build();
        replics.add(withDescription);
        replics.add(withUrl);
        Collections.shuffle(replics);

        when(replicRepo.getAll()).thenReturn(replics);

        List<Replic> filtered = subject.getReplics(null, null, null, null, "the-universe-life-and-everything");

        Assertions.assertEquals(2, filtered.size());
        Assertions.assertTrue(filtered.stream().anyMatch(replic -> replic.getId().equals(withDescription.getId())));
        Assertions.assertTrue(filtered.stream().anyMatch(replic -> replic.getId().equals(withUrl.getId())));
    }

    @Test
    void getReplicsSorts() {
        List<UUID> idList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            idList.add(UUID.randomUUID());
        }

        List<UUID> shuffled = new ArrayList<>(idList);
        Collections.shuffle(shuffled);

        List<ReplicBaseData> replics = new ArrayList<>();
        for (UUID id : shuffled) {
            ReplicBaseData baseData = ReplicBaseDataImpl.builder()
                    .id(id).build();
            replics.add(baseData);
        }

        when(replicRepo.getAll()).thenReturn(replics);

        Comparator<Replic> comparator = Comparator.comparingInt(r -> idList.indexOf(r.getId()));

        List<Replic> sorted = subject.getReplics(comparator, null, null, null, null);

        for (int i = 0; i < idList.size(); i++) {
            Assertions.assertEquals(idList.get(i), sorted.get(i).getId());
        }
    }

    @Test
    void setReplicStateThrowsIfNotFound() {
        UUID specialId = UUID.randomUUID();

        when(replicRepo.getById(specialId)).thenReturn(Optional.empty());

        NotFoundException ex = Assertions.assertThrows(NotFoundException.class, () -> subject.setReplicState(specialId, ReplicState.ACTIVE));
        Assertions.assertEquals(NotFoundSubject.REPLIC, ex.getSubject());
        Assertions.assertEquals(specialId, ex.getIdentifier());
    }

    @Test
    void setReplicStateCallsSaveWithUpdatedStateAndReturns() throws DomainException {
        ReplicBaseData original = ReplicBaseDataImpl.builder()
                .state(ReplicState.ACTIVE).build();

        when(replicRepo.getById(original.getId())).thenReturn(Optional.of(original));
        when(replicRepo.saveModel(any())).thenAnswer(i -> i.getArguments()[0]);

        Replic returned = subject.setReplicState(original.getId(), ReplicState.REMOVED);

        ArgumentCaptor<Replic> replicCaptor = ArgumentCaptor.captor();
        verify(replicRepo, times(1)).saveModel(replicCaptor.capture());

        Assertions.assertEquals(returned, replicCaptor.getValue());
        Assertions.assertEquals(ReplicState.REMOVED, returned.getState());
    }

    @Test
    void visitReplicThrowsForNotFoundReplicOrAccount() {
        UUID replicExistId = UUID.randomUUID();
        UUID replicNoNExistId = UUID.randomUUID();
        UUID accountNoExistId = UUID.randomUUID();

        ReplicBaseData replic = ReplicBaseDataImpl.builder()
                .id(replicExistId).build();

        when(replicRepo.getById(replicExistId)).thenReturn(Optional.of(replic));
        when(replicRepo.getById(replicNoNExistId)).thenReturn(Optional.empty());
        when(accountService.getAccountById(accountNoExistId)).thenReturn(Optional.empty());

        NotFoundException ex1 = Assertions.assertThrows(NotFoundException.class,
                () -> subject.visitReplic(replicNoNExistId, accountNoExistId));
        Assertions.assertEquals(NotFoundSubject.REPLIC, ex1.getSubject());
        Assertions.assertEquals(replicNoNExistId, ex1.getIdentifier());

        NotFoundException ex2 = Assertions.assertThrows(NotFoundException.class,
                () -> subject.visitReplic(replicExistId, accountNoExistId));
        Assertions.assertEquals(NotFoundSubject.ACCOUNT, ex2.getSubject());
        Assertions.assertEquals(accountNoExistId, ex2.getIdentifier());
    }

    @Test
    void visitReplicWorksWithAnonymousAccount() throws DomainException {
        UUID replicId = UUID.randomUUID();
        ReplicBaseData replic = ReplicBaseDataImpl.builder()
                .id(replicId).build();

        when(replicRepo.getById(replicId)).thenReturn(Optional.of(replic));

        when(accessRepo.saveModel(any())).thenAnswer(i -> i.getArguments()[0]);

        ReplicAccess returned = subject.visitReplic(replicId, null);

        ArgumentCaptor<ReplicAccess> accessCaptor = ArgumentCaptor.captor();
        verify(accessRepo, times(1)).saveModel(accessCaptor.capture());

        Assertions.assertEquals(returned, accessCaptor.getValue());
        Assertions.assertEquals(returned.getReplicId(), replic.getId());
        Assertions.assertNull(returned.getVisitorId());
    }

    @Test
    void visitReplicCreatesAndSavesAccessAndReturns() throws DomainException {
        UUID replicId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        ReplicBaseData replic = ReplicBaseDataImpl.builder()
                .id(replicId).build();
        Account account = AccountImpl.builder()
                .id(accountId).build();

        when(replicRepo.getById(replicId)).thenReturn(Optional.of(replic));
        when(accountService.getAccountById(accountId)).thenReturn(Optional.of(account));

        when(accessRepo.saveModel(any())).thenAnswer(i -> i.getArguments()[0]);

        ReplicAccess returned = subject.visitReplic(replicId, accountId);

        ArgumentCaptor<ReplicAccess> accessCaptor = ArgumentCaptor.captor();
        verify(accessRepo, times(1)).saveModel(accessCaptor.capture());

        Assertions.assertEquals(returned, accessCaptor.getValue());
        Assertions.assertEquals(returned.getReplicId(), replic.getId());
        Assertions.assertEquals(returned.getVisitorId(), account.getId());
    }

    @Test
    void receiveContentThrowsForInvalidId() {
        when(replicRepo.getById(any())).thenReturn(Optional.empty());

        UUID id = UUID.randomUUID();

        NotFoundException ex1 = Assertions.assertThrows(NotFoundException.class,
                () -> subject.receiveContent(id, null).close());
        Assertions.assertEquals(NotFoundSubject.REPLIC, ex1.getSubject());
        Assertions.assertEquals(id, ex1.getIdentifier());
    }

    @Test
    void receiveContentThrowsForBadPassword() {
        UUID id = UUID.randomUUID();

        ReplicBaseData replic = ReplicBaseDataImpl.builder()
                .id(id)
                .passwordHash("hash").build();

        when(replicRepo.getById(id)).thenReturn(Optional.of(replic));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> subject.receiveContent(id, "password").close());
    }

    @Test
    void receiveAllowsGoodPasswordAndReturns() throws DomainException {
        UUID id = UUID.randomUUID();

        ReplicBaseData replic = ReplicBaseDataImpl.builder()
                .id(id)
                .passwordHash("hash").build();

        when(replicRepo.getById(id)).thenReturn(Optional.of(replic));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        byte[] buffer = new byte[]{1, 2, 3, 4};
        InputStream dataStream = new ByteArrayInputStream(buffer);
        ReplicFileData fileData = ReplicFileDataImpl.builder()
                .size(42)
                .contentStream(dataStream).build();
        when(fileAccessor.getFileData(id)).thenReturn(fileData);

        InputStream returned = subject.receiveContent(id, "password");

        verify(fileAccessor, times(1)).getFileData(id);
        Assertions.assertEquals(dataStream, returned);
    }

}
