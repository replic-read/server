package com.rere.server.domain.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.rere.server.domain.messaging.EmailSender;
import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.domain.model.account.AuthTokenType;
import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.InvalidTokenException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.OperationDisabledOperation;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.model.impl.AuthTokenImpl;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.repository.AuthTokenRepository;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.BaseDomainServiceTest;
import com.rere.server.domain.service.ServerConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ThrowingConsumer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Contains tests for the {@link AuthenticationServiceImpl} class.
 */
class AuthenticationServiceImplTest extends BaseDomainServiceTest {

    private static final long ACCESS_EXPIRATION = 60 * 1000; // One minute
    private static final String ACCESS_SECRET = "17ee2430-e696-4ec6-8264-2847c50a1a8d";
    private static final long REFRESH_EXPIRATION = 60 * 60 * 1000; // One hour
    private static final long EMAIL_EXPIRATION = 24 * 60 * 60 * 1000; // One day
    @Mock
    private AccountService accountService;
    @Mock
    private AuthTokenRepository tokenRepo;
    @Mock
    private EmailSender emailSender;
    @Mock
    private ServerConfigService configService;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private AccountRepository accountRepo;
    @InjectMocks
    private AuthenticationServiceImpl subject;

    private void throwsForAccountNotFoundTest(ThrowingConsumer<UUID> runnable) {
        UUID specialId = UUID.randomUUID();
        when(accountService.getAccountById(specialId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> runnable.acceptThrows(specialId));
        assertEquals(NotFoundSubject.ACCOUNT, ex.getSubject());
        assertEquals(specialId, ex.getIdentifier());
    }

    @BeforeEach
    void setup() {
        Field accessTokenExpiration = ReflectionUtils.findField(AuthenticationServiceImpl.class, "accessTokenExpiration");
        Field accessTokenSecret = ReflectionUtils.findField(AuthenticationServiceImpl.class, "accessTokenSecret");
        Field refreshTokenExpiration = ReflectionUtils.findField(AuthenticationServiceImpl.class, "refreshTokenExpiration");
        Field emailTokenExpiration = ReflectionUtils.findField(AuthenticationServiceImpl.class, "emailTokenExpiration");

        accessTokenExpiration.setAccessible(true);
        accessTokenSecret.setAccessible(true);
        refreshTokenExpiration.setAccessible(true);
        emailTokenExpiration.setAccessible(true);

        ReflectionUtils.setField(accessTokenExpiration, subject, ACCESS_EXPIRATION);
        ReflectionUtils.setField(accessTokenSecret, subject, ACCESS_SECRET);
        ReflectionUtils.setField(refreshTokenExpiration, subject, REFRESH_EXPIRATION);
        ReflectionUtils.setField(emailTokenExpiration, subject, EMAIL_EXPIRATION);
    }

    private void setAdminCredentials(String email, String username, String password) {
        Field adminEmail = ReflectionUtils.findField(AuthenticationServiceImpl.class, "adminEmail");
        Field adminUsername = ReflectionUtils.findField(AuthenticationServiceImpl.class, "adminUsername");
        Field adminPassword = ReflectionUtils.findField(AuthenticationServiceImpl.class, "adminPassword");

        adminEmail.setAccessible(true);
        adminUsername.setAccessible(true);
        adminPassword.setAccessible(true);

        ReflectionUtils.setField(adminEmail, subject, email);
        ReflectionUtils.setField(adminUsername, subject, username);
        ReflectionUtils.setField(adminPassword, subject, password);
    }

    @Test
    void authenticateWithCredentialsReturnsEmptyForMissingIdentifier() {
        Optional<Account> returned = subject.authenticateWithCredentials(null, null, "password");

        assertTrue(returned.isEmpty());
    }

    @Test
    void authenticateWithCredentialsReturnsOnlyMatchingAccount() {
        List<Account> accounts = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Account account = AccountImpl.builder()
                    .email("user%d@gmail.com".formatted(i))
                    .username("user%d".formatted(i))
                    .passwordHash("password%d".formatted(i))
                    .build();
            accounts.add(account);
        }

        // Mock identity as hashing function
        when(accountService.getAccounts(null, null, null, null)).thenReturn(accounts);
        when(encoder.matches(any(), any())).thenAnswer(inv -> inv.getArguments()[0].equals(inv.getArguments()[1]));

        Optional<Account> returned1 = subject.authenticateWithCredentials("user3@gmail.com", "user3", "password3");
        Optional<Account> returned2 = subject.authenticateWithCredentials("user4@gmail.com", null, "password4");
        Optional<Account> returned3 = subject.authenticateWithCredentials(null, "user5", "password5");
        Optional<Account> returned4 = subject.authenticateWithCredentials(null, "user9", "password1");

        assertTrue(returned1.isPresent());
        assertTrue(returned2.isPresent());
        assertTrue(returned3.isPresent());
        assertTrue(returned4.isEmpty());

        assertEquals(accounts.get(2), returned1.get());
        assertEquals(accounts.get(3), returned2.get());
        assertEquals(accounts.get(4), returned3.get());
    }

    @Test
    void authenticateWithJwtFailsForInvalid() {
        Algorithm algorithm = Algorithm.HMAC256(ACCESS_SECRET);
        Algorithm fakeAlgorithm = Algorithm.HMAC384(ACCESS_SECRET);

        // Invalid algorithm
        String token1 = JWT.create()
                .withIssuer("replic-read-server")
                .withExpiresAt(Instant.now().plusSeconds(60))
                .withSubject(UUID.randomUUID().toString())
                .sign(fakeAlgorithm);
        // Invalid issuer
        String token2 = JWT.create()
                .withIssuer("fake-issuer")
                .withExpiresAt(Instant.now().plusSeconds(60))
                .withSubject(UUID.randomUUID().toString())
                .sign(algorithm);
        // No expiration
        String token3 = JWT.create()
                .withIssuer("replic-read-server")
                .withSubject(UUID.randomUUID().toString())
                .sign(algorithm);
        // No subject
        String token4 = JWT.create()
                .withIssuer("replic-read-server")
                .withExpiresAt(Instant.now().plusSeconds(60))
                .sign(algorithm);
        // No instant as expiration
        String token5 = JWT.create()
                .withIssuer("replic-read-server")
                .withClaim("exp", "I am not an instant")
                .withSubject(UUID.randomUUID().toString())
                .sign(algorithm);
        // No UUID as subject
        String token6 = JWT.create()
                .withIssuer("replic-read-server")
                .withExpiresAt(Instant.now().plusSeconds(60))
                .withSubject("I am not a UUID")
                .sign(algorithm);
        // With expiration before now
        String token7 = JWT.create()
                .withIssuer("replic-read-server")
                .withExpiresAt(Instant.now().minusSeconds(60))
                .withSubject(UUID.randomUUID().toString())
                .sign(algorithm);

        String[] tokens = new String[]{token1, token2, token3, token4, token5, token6, token7};

        for (String token : tokens) {
            Optional<Account> account = subject.authenticateWithJwt(token);
            assertTrue(account.isEmpty());
        }
    }

    @Test
    void authenticateWithJwtCallsAccountServiceAndReturnsFirst() {
        Algorithm algorithm = Algorithm.HMAC256(ACCESS_SECRET);
        Account account = AccountImpl.builder().build();
        String token = JWT.create()
                .withIssuer("replic-read-server")
                .withExpiresAt(Instant.now().plusSeconds(60))
                .withSubject(account.getId().toString())
                .sign(algorithm);

        when(accountService.getAccountById(account.getId())).thenReturn(Optional.of(account));

        Optional<Account> returned = subject.authenticateWithJwt(token);

        assertTrue(returned.isPresent());
        assertEquals(account, returned.get());
    }

    @Test
    void authenticateWithRefreshTokenFailsForNonexistent() {
        List<AuthToken> tokens = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            tokens.add(AuthTokenImpl.builder().build());
        }

        when(tokenRepo.getAll()).thenReturn(tokens);

        Optional<Account> returned = subject.authenticateWithRefreshToken(UUID.randomUUID());

        assertTrue(returned.isEmpty());
    }

    @Test
    void authenticateWithRefreshTokenFailsForType() {
        UUID tokenId = UUID.randomUUID();
        AuthToken token = AuthTokenImpl.builder()
                .id(tokenId)
                .type(AuthTokenType.EMAIL_VERIFICATION)
                .build();

        when(tokenRepo.getAll()).thenReturn(List.of(token));

        Optional<Account> returned = subject.authenticateWithRefreshToken(tokenId);

        assertTrue(returned.isEmpty());
    }

    @Test
    void authenticateWithRefreshTokenFailsForExpiration() {
        UUID tokenId = UUID.randomUUID();
        AuthToken token = AuthTokenImpl.builder()
                .id(tokenId)
                .expirationTimestamp(Instant.now().minusSeconds(60))
                .type(AuthTokenType.REFRESH_TOKEN)
                .build();
        when(tokenRepo.getAll()).thenReturn(List.of(token));

        Optional<Account> returned = subject.authenticateWithRefreshToken(tokenId);

        assertTrue(returned.isEmpty());
    }

    @Test
    void authenticateWithRefreshTokenFailsForInvalidation() {
        UUID tokenId = UUID.randomUUID();
        AuthToken token = AuthTokenImpl.builder()
                .id(tokenId)
                .expirationTimestamp(Instant.now().plusSeconds(60))
                .type(AuthTokenType.REFRESH_TOKEN)
                .invalidated(true)
                .build();

        when(tokenRepo.getAll()).thenReturn(List.of(token));

        Optional<Account> returned = subject.authenticateWithRefreshToken(tokenId);

        assertTrue(returned.isEmpty());
    }

    @Test
    void authenticateWithRefreshTokenReturnsAccount() {
        Account account = AccountImpl.builder().build();
        AuthToken token = AuthTokenImpl.builder()
                .expirationTimestamp(Instant.now().plusSeconds(60))
                .type(AuthTokenType.REFRESH_TOKEN)
                .accountId(account.getId())
                .build();

        when(tokenRepo.getAll()).thenReturn(List.of(token));
        when(accountService.getAccountById(account.getId())).thenReturn(Optional.of(account));

        Optional<Account> returned = subject.authenticateWithRefreshToken(token.getToken());

        assertTrue(returned.isPresent());
        assertEquals(account, returned.get());
    }

    @Test
    void createRefreshTokenThrowsForAccountNotFound() {
        throwsForAccountNotFoundTest(id -> subject.createRefreshToken(id));
    }

    @Test
    void createRefreshTokenCallsRepoWithGoodToken() throws DomainException {
        Account account = AccountImpl.builder().build();

        when(accountService.getAccountById(account.getId())).thenReturn(Optional.of(account));
        when(tokenRepo.saveModel(any())).thenAnswer(inv -> inv.getArguments()[0]);

        UUID returned = subject.createRefreshToken(account.getId());

        ArgumentCaptor<AuthToken> tokenCaptor = ArgumentCaptor.forClass(AuthToken.class);
        verify(tokenRepo, times(1)).saveModel(tokenCaptor.capture());

        assertEquals(returned, tokenCaptor.getValue().getToken());
        assertEquals(AuthTokenType.REFRESH_TOKEN, tokenCaptor.getValue().getType());
        assertEquals(REFRESH_EXPIRATION, tokenCaptor.getValue().getExpirationTimestamp().toEpochMilli() - tokenCaptor.getValue().getCreationTimestamp().toEpochMilli());
    }

    @Test
    void createAccessTokenThrowsForAccountNotFound() {
        throwsForAccountNotFoundTest(id -> subject.createAccessToken(id));
    }

    @Test
    void createAccessTokenReturnsValidToken() throws DomainException {
        Account account = AccountImpl.builder().build();

        when(accountService.getAccountById(account.getId())).thenReturn(Optional.of(account));

        String token = subject.createAccessToken(account.getId());

        Optional<Account> returned = subject.authenticateWithJwt(token);

        assertTrue(returned.isPresent());
        assertEquals(account, returned.get());
    }

    @Test
    void requestEmailVerificationThrowsForAccountNotFound() {
        throwsForAccountNotFoundTest(id -> subject.requestEmailVerification(id));
    }

    @Test
    void requestEmailVerificationSavesTokenAndSendsEmail() throws DomainException {
        Account account = AccountImpl.builder().build();

        when(accountService.getAccountById(account.getId())).thenReturn(Optional.of(account));
        when(tokenRepo.saveModel(any())).thenAnswer(inv -> inv.getArguments()[0]);
        when(emailSender.sendVerificationToken(any(), any(), anyBoolean())).thenReturn(true);

        subject.requestEmailVerification(account.getId());

        ArgumentCaptor<AuthToken> repoCaptor = ArgumentCaptor.captor();
        ArgumentCaptor<AuthToken> emailCaptor = ArgumentCaptor.captor();

        verify(tokenRepo, times(1)).saveModel(repoCaptor.capture());
        verify(emailSender, times(1)).sendVerificationToken(any(), emailCaptor.capture(), anyBoolean());

        assertEquals(repoCaptor.getValue(), emailCaptor.getValue());
        assertEquals(AuthTokenType.EMAIL_VERIFICATION, repoCaptor.getValue().getType());
        assertFalse(repoCaptor.getValue().isInvalidated());
        assertEquals(EMAIL_EXPIRATION, repoCaptor.getValue().getExpirationTimestamp().toEpochMilli() - repoCaptor.getValue().getCreationTimestamp().toEpochMilli());
    }

    @Test
    void createAccountThrowsIfNotAllowedOrBypassed() {
        ServerConfig config = new ServerConfigImpl(AuthUserGroup.ALL, AuthUserGroup.ALL, AuthUserGroup.ALL, false, null, Period.of(1, 0, 0));
        when(configService.get()).thenReturn(config);

        OperationDisabledException ex = assertThrows(OperationDisabledException.class, () -> subject.createAccount("", "", "", 0, false, false, false));
        assertEquals(OperationDisabledOperation.SIGNUP, ex.getOperation());
    }

    @Test
    void createAccountSavesAccountAndSendsEmailIfRequested() throws DomainException {
        ServerConfig config = new ServerConfigImpl(AuthUserGroup.ALL, AuthUserGroup.ALL, AuthUserGroup.ALL, true, null, Period.of(1, 0, 0));

        when(configService.get()).thenReturn(config);
        when(accountRepo.saveModel(any())).thenAnswer(inv -> inv.getArguments()[0]);
        when(encoder.encode("password")).thenReturn("passwordhash");
        when(accountService.getAccountById(any())).thenReturn(Optional.of(AccountImpl.builder().build()));
        when(accountService.getByEmail(any())).thenReturn(Optional.empty());
        when(accountService.getByUsername(any())).thenReturn(Optional.empty());

        Account returned1 = subject.createAccount("email@gmail.com", "user123", "password", 0, false, false, false);
        Account returned2 = subject.createAccount("email@gmail.com", "user123", "password", 0, false, true, false);

        verify(emailSender, times(1)).sendVerificationToken(any(), any(), anyBoolean());

        assertEquals("email@gmail.com", returned1.getEmail());
        assertEquals("user123", returned1.getUsername());
        assertEquals("passwordhash", returned1.getPasswordHash());
        assertEquals("email@gmail.com", returned2.getEmail());
        assertEquals("user123", returned2.getUsername());
        assertEquals("passwordhash", returned2.getPasswordHash());
    }

    @Test
    void createAccountThrowsForNonUniqueEmailOrUsername() {
        Account acc = AccountImpl.builder().build();
        when(accountService.getByEmail("email1")).thenReturn(Optional.of(acc));
        when(accountService.getByEmail("email5")).thenReturn(Optional.empty());
        when(accountService.getByUsername("username5")).thenReturn(Optional.empty());
        when(accountService.getByUsername("username2")).thenReturn(Optional.of(acc));

        when(configService.get()).thenReturn(
                new ServerConfigImpl(AuthUserGroup.ALL, AuthUserGroup.ALL, AuthUserGroup.ALL,
                        true, null, Period.of(1, 0, 0))
        );

        NotUniqueException ex1 = assertThrows(NotUniqueException.class,
                () -> subject.createAccount("email1", "username5", "password5", 0, false, false, true));
        assertEquals(NotUniqueSubject.EMAIL, ex1.getSubject());

        NotUniqueException ex2 = assertThrows(NotUniqueException.class,
                () -> subject.createAccount("email5", "username2", "password5", 0, false, false, true));
        assertEquals(NotUniqueSubject.USERNAME, ex2.getSubject());
    }

    @Test
    void ensureSingletonAdminCreatesNewAdminAccount() throws DomainException {
        // Premise: We have one admin account in the system
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Account account = AccountImpl.builder()
                    .email("user%d@gmail.com".formatted(i))
                    .username("user%d".formatted(i))
                    .passwordHash("password%d".formatted(i))
                    .build();
            accounts.add(account);
        }
        Account oldAdminAccount = new AccountImpl(UUID.randomUUID(), Instant.now(), "admin.old@gmail.com", "old-admin", "old-password-hash", true, AccountState.ACTIVE, 0);
        accounts.add(oldAdminAccount);
        ServerConfig config = new ServerConfigImpl(AuthUserGroup.ALL, AuthUserGroup.ALL, AuthUserGroup.ALL, true, null, Period.of(1, 0, 0));

        when(accountService.getAccounts(null, null, null, null)).thenReturn(accounts);
        when(accountService.getByEmail(any())).thenReturn(Optional.empty());
        when(accountService.getByUsername(any())).thenReturn(Optional.empty());
        when(accountRepo.saveModel(any())).thenAnswer(inv -> inv.getArguments()[0]);
        when(encoder.encode(any())).thenAnswer(inv -> inv.getArguments()[0]);
        when(configService.get()).thenReturn(config);

        setAdminCredentials("admin.new@gmail.com", "new-admin", "new-password");
        Account newAdminAccount = subject.ensureSingletonAdmin();

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo, times(2)).saveModel(accountCaptor.capture());

        // First call to accountRepo.save() is making the old admin account non-admin.
        assertEquals(oldAdminAccount.getId(), accountCaptor.getAllValues().getFirst().getId());
        assertFalse(accountCaptor.getAllValues().getFirst().isAdmin());

        // Second call to accountRepo.save() is saving the new admin account
        assertEquals(accountCaptor.getAllValues().get(1), newAdminAccount);
        assertEquals("admin.new@gmail.com", accountCaptor.getAllValues().get(1).getEmail());
        assertEquals("new-admin", accountCaptor.getAllValues().get(1).getUsername());
        assertEquals("new-password", accountCaptor.getAllValues().get(1).getPasswordHash());
    }

    @Test
    void ensureSingletonAdminThrowsForNotUnique() {
        List<Account> accounts = List.of(
                AccountImpl.builder().email("1@gmail.com").username("1").passwordHash("1").build(),
                AccountImpl.builder().email("2@gmail.com").username("2").passwordHash("2").build()
        );

        when(accountService.getAccounts(null, null, null, null)).thenReturn(accounts);

        // Both acc's will be found but different
        setAdminCredentials("1@gmail.com", "2", "");
        assertThrows(NotUniqueException.class,
                () -> subject.ensureSingletonAdmin());

        // Email acc will be found, username acc not.
        setAdminCredentials("1@gmail.com", "3", "");
        assertThrows(NotUniqueException.class,
                () -> subject.ensureSingletonAdmin());

        // Username acc will be found, email acc not.
        setAdminCredentials("3@gmail.com", "1", "");
        assertThrows(NotUniqueException.class,
                () -> subject.ensureSingletonAdmin());
    }

    @Test
    void ensureSingletonAdminUpdatesExistingAccount() throws DomainException {
        // Premise: We have one admin account in the system
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Account account = AccountImpl.builder()
                    .email("user%d@gmail.com".formatted(i))
                    .username("user%d".formatted(i))
                    .passwordHash("password%d".formatted(i))
                    .build();
            accounts.add(account);
        }
        Account oldAdminAccount = new AccountImpl(UUID.randomUUID(), Instant.now(), "admin.old@gmail.com", "old-admin", "old-password-hash", true, AccountState.ACTIVE, 0);
        accounts.add(oldAdminAccount);

        when(accountService.getAccounts(null, null, null, null)).thenReturn(accounts);

        setAdminCredentials("user1@gmail.com", "user1", "password1");
        subject.ensureSingletonAdmin();

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo, times(2)).saveModel(accountCaptor.capture());

        // First call to accountRepo.save() is to make old admin not admin anymore.
        assertEquals(oldAdminAccount.getId(), accountCaptor.getAllValues().getFirst().getId());
        assertFalse(accountCaptor.getAllValues().getFirst().isAdmin());

        // Second call to accountRepo.save() is to make new account to admin
        assertEquals("user1@gmail.com", accountCaptor.getAllValues().get(1).getEmail());
        assertEquals("user1", accountCaptor.getAllValues().get(1).getUsername());
        assertEquals("password1", accountCaptor.getAllValues().get(1).getPasswordHash());
        assertTrue(accountCaptor.getAllValues().get(1).isAdmin());
    }

    @Test
    void validateEmailWorks() throws DomainException {
        Account account = AccountImpl.builder().build();
        AuthToken expiredToken = AuthTokenImpl.builder()
                .expirationTimestamp(Instant.now().minusSeconds(60))
                .accountId(account.getId())
                .type(AuthTokenType.EMAIL_VERIFICATION)
                .build();

        AuthToken invalidatedToken = AuthTokenImpl.builder()
                .expirationTimestamp(Instant.now().plusSeconds(60))
                .accountId(account.getId())
                .type(AuthTokenType.EMAIL_VERIFICATION)
                .invalidated(true)
                .build();

        AuthToken wrongTypeToken = AuthTokenImpl.builder()
                .expirationTimestamp(Instant.now().plusSeconds(60))
                .accountId(account.getId())
                .type(AuthTokenType.REFRESH_TOKEN)
                .invalidated(false)
                .build();

        UUID nonExistingTokenToken = UUID.randomUUID();

        AuthToken validToken = AuthTokenImpl.builder()
                .expirationTimestamp(Instant.now().plusSeconds(60))
                .accountId(account.getId())
                .type(AuthTokenType.EMAIL_VERIFICATION)
                .invalidated(false)
                .build();

        when(tokenRepo.getAll()).thenReturn(List.of(expiredToken, invalidatedToken, wrongTypeToken, validToken));
        when(accountService.getAccountById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(InvalidTokenException.class, () -> subject.validateEmail(expiredToken.getToken()));
        assertThrows(InvalidTokenException.class, () -> subject.validateEmail(invalidatedToken.getToken()));
        assertThrows(InvalidTokenException.class, () -> subject.validateEmail(wrongTypeToken.getToken()));
        assertThrows(InvalidTokenException.class, () -> subject.validateEmail(nonExistingTokenToken));

        Account accountForExistingToken = subject.validateEmail(validToken.getToken());
        assertEquals(account, accountForExistingToken);
    }

}
