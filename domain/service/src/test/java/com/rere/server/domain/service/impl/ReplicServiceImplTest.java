package com.rere.server.domain.service.impl;

import com.rere.server.domain.io.ReplicFileAccessor;
import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.exception.DomainException;
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
import com.rere.server.domain.service.BaseDomainServiceTest;
import com.rere.server.domain.service.ServerConfigService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
    private ReplicAccessRepository accessRepo;
    @Mock
    private AccountRepository accountRepo;
    @Mock
    private ReplicFileAccessor fileAccessor;
    @Mock
    private ServerConfigService configService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private ReplicServiceImpl subject;

    private static ServerConfig createConfig(ReplicLimitConfig limit, Period expirationPeriod) {
        return new ServerConfig(AuthUserGroup.ALL, AuthUserGroup.ALL, AuthUserGroup.ALL, true, limit, expirationPeriod);
    }

    private static Replic createReplic(Account owner, Instant creation, ReplicState state) {
        return new Replic(
                new BaseReplic(UUID.randomUUID(), creation != null ? creation : Instant.now(), URL, MediaMode.ALL, state != null ? state : ReplicState.ACTIVE, null, null, null, owner),
                42
        );
    }

    private static Replic createReplic(UUID id) {
        return new Replic(
                new BaseReplic(id, Instant.now(), URL, MediaMode.ALL, ReplicState.REMOVED, null, null, null, null),
                42
        );
    }

    private static Account createAccount(UUID id) {
        return new Account(id, Instant.now(), "", "", "", false, AccountState.ACTIVE, 0);
    }

    @Test
    void createReplicThrowsForQuotaViolation() {
        /*
         * Period start was 12 days ago and lasts 5 days, so it looks like:
         * | . . . . . | . . . . . / . . !
         * where '.' is a passed day and '!' is today. '|' is the start of a new period, and '/' is the start of the current period.
         */
        Instant now = Instant.now();
        Instant periodStart = now.minus(12, ChronoUnit.DAYS);
        ServerConfig config = createConfig(new ReplicLimitConfig(Period.of(0, 0, 5), 3, periodStart), null);
        when(configService.get()).thenReturn(config);

        Account account = createAccount(UUID.randomUUID());
        Account accountFake = createAccount(UUID.randomUUID());

        // There are three replics in the current period created by 'account'. The last one is a fake to test the filtering.
        Replic replic1 = createReplic(account, now.minusSeconds(300), null);
        Replic replic2 = createReplic(account, now.minusSeconds(600), null);
        Replic replic3 = createReplic(account, now.minusSeconds(900), null);
        Replic replic4 = createReplic(accountFake, now.minusSeconds(1200), null);
        when(replicRepo.getAll()).thenReturn(List.of(replic1, replic2, replic3, replic4));

        ReplicQuotaMetException ex = Assertions.assertThrows(ReplicQuotaMetException.class, () -> {
            subject.createReplic(URL, MediaMode.ALL, null, null, null, account, file -> false);
        });
        Assertions.assertEquals(account, ex.getAccount());
    }

    @Test
    void createReplicThrowsForMissingExpiration() {
        ServerConfig config = createConfig(null, Period.of(1, 2, 3));
        when(configService.get()).thenReturn(config);

        InvalidExpirationException ex = Assertions.assertThrows(InvalidExpirationException.class,
                () -> subject.createReplic(URL, MediaMode.ALL, null, null, null, null, file -> false));
        Assertions.assertTrue(ex.isExpirationMissing());
    }

    @Test
    void createReplicThrowsForInvalidExpiration() {
        Instant now = Instant.now();
        ServerConfig config = createConfig(null, Period.of(0, 0, 5));
        when(configService.get()).thenReturn(config);

        // Attempt to create replic that expires 6 days in the future, but 5 is maximum allowed
        InvalidExpirationException ex = Assertions.assertThrows(InvalidExpirationException.class,
                () -> subject.createReplic(URL, MediaMode.ALL, null, now.plus(6, ChronoUnit.DAYS), null, null, file -> false));
        Assertions.assertFalse(ex.isExpirationMissing());
    }

    @Test
    void createReplicUsesFileCallbackAndThrowsForFailure() {
        ServerConfig config = createConfig(null, null);
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
        ServerConfig config = createConfig(null, null);
        when(configService.get()).thenReturn(config);

        // Attempt to create replic that expires 6 days in the future, but 5 is maximum allowed
        when(fileAccessor.getDataSize(any())).thenReturn(42L);
        when(replicRepo.save(any())).thenAnswer((Answer<Replic>) invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(any())).thenReturn("hash");

        Replic createdReplic = subject.createReplic(URL, MediaMode.ALL, null, null, null, null, file -> true);

        ArgumentCaptor<Replic> replicCaptor = ArgumentCaptor.forClass(Replic.class);
        verify(replicRepo, new Times(1)).save(replicCaptor.capture());

        Assertions.assertEquals(createdReplic, replicCaptor.getValue());

        Assertions.assertEquals(42L, replicCaptor.getValue().getSize());
        Assertions.assertEquals(URL, replicCaptor.getValue().getOriginalUrl());
    }

    @Test
    void getReplicsFiltersAccountId() {
        Account acc1 = createAccount(UUID.randomUUID());
        Account acc2 = createAccount(UUID.randomUUID());
        List<Replic> replics = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            replics.add(createReplic(acc1, null, null));
        }
        for (int i = 0; i < 5; i++) {
            replics.add(createReplic(acc2, null, null));
        }
        Collections.shuffle(replics);

        when(replicRepo.getAll()).thenReturn(replics);

        List<Replic> filtered = subject.getReplics(null, null, acc1.getId(), null, null);

        Assertions.assertEquals(5, filtered.size());
        for (Replic replic : filtered) {
            Assertions.assertEquals(acc1.getId(), replic.getOwner().getId());
        }
    }

    @Test
    void getReplicsFiltersReplicId() {
        UUID specialId = UUID.randomUUID();
        List<Replic> replics = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            replics.add(createReplic(null, null, null));
        }
        replics.add(new Replic(new BaseReplic(specialId, Instant.now(), URL, MediaMode.ALL, ReplicState.ACTIVE, null, null, null, null), 42));
        Collections.shuffle(replics);

        when(replicRepo.getAll()).thenReturn(replics);

        List<Replic> filtered = subject.getReplics(null, specialId, null, null, null);

        Assertions.assertEquals(1, filtered.size());
        Assertions.assertEquals(specialId, filtered.getFirst().getId());
    }

    @Test
    void getReplicsFiltersReplicState() {
        List<Replic> replics = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            replics.add(createReplic(null, null, ReplicState.ACTIVE));
            replics.add(createReplic(null, null, ReplicState.INACTIVE));
            replics.add(createReplic(null, null, ReplicState.REMOVED));
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
        List<Replic> replics = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            replics.add(createReplic(null, null, null));
        }
        Replic withDescription = new Replic(
                new BaseReplic(UUID.randomUUID(), Instant.now(), URL, MediaMode.ALL, ReplicState.ACTIVE, "adsnksdn" + "the-universe-life-and-everything" + "sdfdsf", null, null, null),
                42
        );
        URL specialUrl = URI.create("https://the-universe-life-and-everything.com/").toURL();
        Replic withUrl = new Replic(
                new BaseReplic(UUID.randomUUID(), Instant.now(), specialUrl, MediaMode.ALL, ReplicState.ACTIVE, null, null, null, null),
                42
        );
        replics.add(withDescription);
        replics.add(withUrl);
        Collections.shuffle(replics);

        when(replicRepo.getAll()).thenReturn(replics);

        List<Replic> filtered = subject.getReplics(null, null, null, null, "the-universe-life-and-everything");

        Assertions.assertEquals(2, filtered.size());
        Assertions.assertTrue(filtered.contains(withDescription));
        Assertions.assertTrue(filtered.contains(withUrl));
    }

    @Test
    void getReplicsSorts() {
        List<UUID> idList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            idList.add(UUID.randomUUID());
        }

        List<UUID> shuffled = new ArrayList<>(idList);
        Collections.shuffle(shuffled);

        List<Replic> replics = new ArrayList<>();
        for (UUID id : shuffled) {
            replics.add(new Replic(
                    new BaseReplic(id, Instant.now(), URL, MediaMode.ALL, ReplicState.ACTIVE, null, null, null, null),
                    42
            ));
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
        List<Replic> replics = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            replics.add(createReplic(null, null, null));
        }

        when(replicRepo.getAll()).thenReturn(replics);

        NotFoundException ex = Assertions.assertThrows(NotFoundException.class, () -> subject.setReplicState(specialId, ReplicState.ACTIVE));
        Assertions.assertEquals(NotFoundSubject.REPLIC, ex.getSubject());
        Assertions.assertEquals(specialId, ex.getIdentifier());
    }

    @Test
    void setReplicCallsSaveWithUpdatedStateAndReturns() throws DomainException {
        List<Replic> replics = new ArrayList<>();
        Replic original = createReplic(null, null, ReplicState.ACTIVE);
        replics.add(original);

        when(replicRepo.getAll()).thenReturn(replics);
        when(replicRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Replic returned = subject.setReplicState(original.getId(), ReplicState.REMOVED);

        ArgumentCaptor<Replic> replicCaptor = ArgumentCaptor.forClass(Replic.class);
        verify(replicRepo, times(1)).save(replicCaptor.capture());

        Assertions.assertEquals(returned, replicCaptor.getValue());
        Assertions.assertEquals(ReplicState.REMOVED, returned.getState());
    }

    @Test
    void visitReplicThrowsForNotFoundReplicOrAccount() {
        UUID replicExistId = UUID.randomUUID();
        UUID replicNoNExistId = UUID.randomUUID();
        UUID accountNoExistId = UUID.randomUUID();

        Replic replic = createReplic(replicExistId);

        when(replicRepo.getById(replicExistId)).thenReturn(Optional.of(replic));
        when(replicRepo.getById(replicNoNExistId)).thenReturn(Optional.empty());
        when(accountRepo.getById(accountNoExistId)).thenReturn(Optional.empty());

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
        Replic replic = createReplic(replicId);

        when(replicRepo.getById(replicId)).thenReturn(Optional.of(replic));

        when(accessRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        ReplicAccess returned = subject.visitReplic(replicId, null);

        ArgumentCaptor<ReplicAccess> accessCaptor = ArgumentCaptor.forClass(ReplicAccess.class);
        verify(accessRepo, times(1)).save(accessCaptor.capture());

        Assertions.assertEquals(returned, accessCaptor.getValue());
        Assertions.assertEquals(returned.replic(), replic);
        Assertions.assertNull(returned.visitor());
    }

    @Test
    void visitReplicCreatesAndSavesAccessAndReturns() throws DomainException {
        UUID replicId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Replic replic = createReplic(replicId);
        Account account = createAccount(accountId);

        when(replicRepo.getById(replicId)).thenReturn(Optional.of(replic));
        when(accountRepo.getById(accountId)).thenReturn(Optional.of(account));

        when(accessRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        ReplicAccess returned = subject.visitReplic(replicId, accountId);

        ArgumentCaptor<ReplicAccess> accessCaptor = ArgumentCaptor.forClass(ReplicAccess.class);
        verify(accessRepo, times(1)).save(accessCaptor.capture());

        Assertions.assertEquals(returned, accessCaptor.getValue());
        Assertions.assertEquals(returned.replic(), replic);
        Assertions.assertEquals(returned.visitor(), account);
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

        Replic replic = new Replic(
                new BaseReplic(id, Instant.now(), URL, MediaMode.ALL, ReplicState.ACTIVE, null, null, "hash", null),
                42
        );

        when(replicRepo.getById(id)).thenReturn(Optional.of(replic));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> subject.receiveContent(id, "password").close());
    }

    @Test
    void receiveAllowsGoodPasswordAndReturns() throws DomainException {
        UUID id = UUID.randomUUID();

        Replic replic = new Replic(
                new BaseReplic(id, Instant.now(), URL, MediaMode.ALL, ReplicState.ACTIVE, null, null, "hash", null),
                42
        );

        when(replicRepo.getById(id)).thenReturn(Optional.of(replic));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        byte[] buffer = new byte[]{1, 2, 3, 4};
        InputStream dataStream = new ByteArrayInputStream(buffer);
        when(fileAccessor.getDataStream(id)).thenReturn(dataStream);

        InputStream returned = subject.receiveContent(id, "password");

        verify(fileAccessor, times(1)).getDataStream(id);
        Assertions.assertEquals(dataStream, returned);
    }

}
