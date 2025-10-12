package com.rere.server.domain.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rere.server.domain.messaging.EmailSender;
import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.domain.model.account.AuthTokenType;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.exception.InvalidTokenException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.exception.OperationDisabledOperation;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.model.impl.AuthTokenImpl;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.repository.AuthTokenRepository;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.ServerConfigService;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the authentication service.
 */
@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String JWT_ISSUER = "replic-read-server";

    private static final String JWT_EXPIRATION = "exp";

    private static final String JWT_SUBJECT = "sub";

    private final AccountService accountService;

    private final AuthTokenRepository tokenRepo;

    private final EmailSender emailSender;

    private final ServerConfigService configService;

    private final PasswordEncoder encoder;

    private final AccountRepository accountRepo;

    @Value("${rere.auth.access.expiration}")
    private long accessTokenExpiration;

    @Value("${rere.auth.access.secret}")
    private String accessTokenSecret;

    @Value("${rere.auth.refresh.expiration}")
    private long refreshTokenExpiration;

    @Value("${rere.auth.email.expiration}")
    private long emailTokenExpiration;

    @Value("${rere.admin.email}")
    private String adminEmail;

    @Value("${rere.admin.username}")
    private String adminUsername;

    @Value("${rere.admin.password}")
    private String adminPassword;

    @Autowired
    public AuthenticationServiceImpl(AccountService accountService, AuthTokenRepository tokenRepo, EmailSender emailSender, ServerConfigService configService, PasswordEncoder encoder, AccountRepository accountRepo) {
        this.accountService = accountService;
        this.tokenRepo = tokenRepo;
        this.emailSender = emailSender;
        this.configService = configService;
        this.encoder = encoder;
        this.accountRepo = accountRepo;
    }

    private Algorithm createAlgorithm() {
        return Algorithm.HMAC256(accessTokenSecret);
    }

    @Nonnull
    @Override
    public Optional<Account> authenticateWithCredentials(String email, String username, @Nonnull String password) {
        if (email == null && username == null) {
            return Optional.empty();
        }
        return accountService.getAccounts(null, null, null, null)
                .stream()
                .filter(account -> (email == null || account.getEmail().equals(email)) &&
                                   (username == null) || account.getUsername().equals(username))
                .filter(account -> encoder.matches(password, account.getPasswordHash()))
                .findFirst();
    }

    @Nonnull
    @Override
    public Optional<Account> authenticateWithJwt(@Nonnull String jwt) {
        JWTVerifier verifier = JWT
                .require(createAlgorithm())
                .withIssuer(JWT_ISSUER)
                .withClaimPresence(JWT_EXPIRATION)
                .withClaimPresence(JWT_SUBJECT)
                .build();

        DecodedJWT decoded;
        try {
            decoded = verifier.verify(jwt);
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }

        Instant expiration = decoded.getExpiresAtAsInstant();
        if (expiration == null) {
            return Optional.empty();
        }

        UUID accountId;
        try {
            accountId = UUID.fromString(decoded.getSubject());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        if (expiration.isBefore(Instant.now())) {
            return Optional.empty();
        }

        return accountService.getAccountById(accountId);
    }

    @Nonnull
    @Override
    public Optional<Account> authenticateWithRefreshToken(@Nonnull UUID refreshToken) {
        AuthToken token = tokenRepo
                .getAll().stream()
                .filter(t -> t.getToken().equals(refreshToken))
                .findFirst()
                .orElse(null);

        if (token == null ||
            !token.isValid(AuthTokenType.REFRESH_TOKEN, Instant.now())
        ) {
            return Optional.empty();
        }

        return accountService
                .getAccountById(token.getAccountId());
    }

    @Nonnull
    @Override
    public String createAccessToken(@Nonnull UUID accountId) throws NotFoundException {
        Account account = accountService
                .getAccountById(accountId)
                .orElseThrow(() -> new NotFoundException(NotFoundSubject.ACCOUNT, accountId));

        return JWT.create()
                .withIssuer(JWT_ISSUER)
                .withSubject(account.getId().toString())
                .withExpiresAt(Instant.now().plusMillis(accessTokenExpiration))
                .sign(createAlgorithm());
    }

    @Nonnull
    @Override
    public UUID createRefreshToken(@Nonnull UUID accountId) throws NotFoundException {
        Account account = accountService
                .getAccountById(accountId)
                .orElseThrow(() -> new NotFoundException(NotFoundSubject.ACCOUNT, accountId));

        Instant now = Instant.now();
        AuthToken token = AuthTokenImpl.builder()
                .creationTimestamp(now)
                .expirationTimestamp(now.plusMillis(refreshTokenExpiration))
                .accountId(account.getId())
                .type(AuthTokenType.REFRESH_TOKEN)
                .build();

        return tokenRepo.saveModel(token).getToken();
    }

    @Nonnull
    @Override
    public Account createAccount(@Nonnull String email, @Nonnull String username, @Nonnull String password, int profileColor, boolean isAdmin, boolean isVerified, boolean sendEmail, boolean bypassConfig) throws NotUniqueException, OperationDisabledException {
        ServerConfig config = configService.get();

        if (!config.isAllowAccountCreation() && !bypassConfig) {
            throw new OperationDisabledException(OperationDisabledOperation.SIGNUP);
        }

        boolean isEmailUnique = accountService.getByEmail(email).isEmpty();
        boolean isAccountUnique = accountService.getByUsername(username).isEmpty();

        if (!isEmailUnique) {
            throw new NotUniqueException(NotUniqueSubject.EMAIL);
        }
        if (!isAccountUnique) {
            throw new NotUniqueException(NotUniqueSubject.USERNAME);
        }

        AccountState initialState = isVerified ? AccountState.ACTIVE : AccountState.UNVERIFIED;

        Account account = AccountImpl.builder()
                .email(email)
                .username(username)
                .passwordHash(encoder.encode(password))
                .isAdmin(isAdmin)
                .accountState(initialState)
                .profileColor(profileColor)
                .build();
        account = accountRepo.saveModel(account);

        if (!isVerified && sendEmail) {
            try {
                requestEmailVerification(account.getId(), true);
            } catch (NotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        return account;
    }

    @Nonnull
    @Override
    public Account ensureSingletonAdmin() throws NotUniqueException {
        Account emailMatchingAccount = accountService
                .getAccounts(null, null, null, null)
                .stream()
                .filter(account -> account.getEmail().equals(adminEmail))
                .findFirst()
                .orElse(null);
        Account usernameMatchingAccount = accountService
                .getAccounts(null, null, null, null)
                .stream()
                .filter(account -> account.getUsername().equals(adminUsername))
                .findFirst()
                .orElse(null);

        if (emailMatchingAccount == null && usernameMatchingAccount == null) { // We can create a new admin account
            unAdminifyAllAdminAccounts();
            try {
                return createAccount(
                        adminEmail,
                        adminUsername,
                        encoder.encode(adminPassword),
                        0,
                        true,
                        true,
                        false,
                        true
                );
            } catch (OperationDisabledException e) {
                // OperationDisabledException is never thrown because we bypass the config.
                throw new IllegalStateException(e);
            }
        } else if (emailMatchingAccount == null ||
                   !emailMatchingAccount.equals(usernameMatchingAccount)
        ) { // The accounts for email and username are different. We can't set one specific account to admin.
            // The subject is nonsense, but we need to pass one.
            throw new NotUniqueException(NotUniqueSubject.EMAIL);
        } else { // The accounts are equal and not null.
            unAdminifyAllAdminAccounts();
            emailMatchingAccount.setAdmin(true);
            return accountRepo.saveModel(emailMatchingAccount);
        }
    }

    private void unAdminifyAllAdminAccounts() {
        accountService
                .getAccounts(null, null, null, null)
                .stream()
                .filter(Account::isAdmin)
                .forEach(account -> {
                    account.setAdmin(false);
                    accountRepo.saveModel(account);
                });
    }

    @Override
    public void requestEmailVerification(@Nonnull UUID accountId, boolean html) throws NotFoundException {
        Account account = accountService
                .getAccountById(accountId)
                .orElseThrow(() -> new NotFoundException(NotFoundSubject.ACCOUNT, accountId));

        Instant now = Instant.now();
        AuthToken emailToken = AuthTokenImpl.builder()
                .creationTimestamp(now)
                .expirationTimestamp(now.plusMillis(emailTokenExpiration))
                .accountId(account.getId())
                .type(AuthTokenType.EMAIL_VERIFICATION)
                .build();

        emailToken = tokenRepo.saveModel(emailToken);

        emailSender.sendVerificationToken(account, emailToken, html);
    }

    @Nonnull
    @Override
    public Account validateEmail(@Nonnull UUID authToken) throws InvalidTokenException {
        return tokenRepo
                .getAll()
                .stream()
                .filter(t -> t.getToken().equals(authToken))
                .findFirst()
                .flatMap(t -> t.isValid(AuthTokenType.EMAIL_VERIFICATION, Instant.now()) ? Optional.of(t) : Optional.empty())
                .map(AuthToken::getAccountId)
                .flatMap(accountService::getAccountById)
                .orElseThrow(InvalidTokenException::new);
    }
}
