package com.rere.server.inter.authorization.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.model.impl.ReplicImpl;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.AuthorizationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Contains test for the {@link RoleGroupBasedAuthorizer} class.
 */
@ExtendWith(MockitoExtension.class)
class RoleGroupBasedAuthorizerTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private ServerConfigService configService;
    @InjectMocks
    private RoleGroupBasedAuthorizer subject;

    @SuppressWarnings("ConstantValue")
    private void testGroupBased(Consumer<Account> call) {
        Account unauthenticated = null;
        Account unverified = AccountImpl.builder()
                .accountState(AccountState.UNVERIFIED)
                .build();
        Account verified = AccountImpl.builder()
                .accountState(AccountState.ACTIVE)
                .build();

        // ALL
        ServerConfig configAll = ServerConfigImpl.builder()
                .accessReplicsGroup(AuthUserGroup.ALL)
                .createReplicsGroup(AuthUserGroup.ALL)
                .createReportsGroup(AuthUserGroup.ALL)
                .build();
        when(configService.get()).thenReturn(configAll);

        assertDoesNotThrow(() -> call.accept(unauthenticated));
        assertDoesNotThrow(() -> call.accept(unverified));
        assertDoesNotThrow(() -> call.accept(verified));

        // ACCOUNT
        ServerConfig configAccount = ServerConfigImpl.builder()
                .accessReplicsGroup(AuthUserGroup.ACCOUNT)
                .createReplicsGroup(AuthUserGroup.ACCOUNT)
                .createReportsGroup(AuthUserGroup.ACCOUNT)
                .build();
        when(configService.get()).thenReturn(configAccount);

        assertThrows(AuthorizationException.class,
                () -> call.accept(unauthenticated));
        assertDoesNotThrow(() -> call.accept(unverified));
        assertDoesNotThrow(() -> call.accept(verified));

        // VERIFIED
        ServerConfig configVerified = ServerConfigImpl.builder()
                .accessReplicsGroup(AuthUserGroup.VERIFIED)
                .createReplicsGroup(AuthUserGroup.VERIFIED)
                .createReportsGroup(AuthUserGroup.VERIFIED)
                .build();
        when(configService.get()).thenReturn(configVerified);

        assertThrows(AuthorizationException.class,
                () -> call.accept(unauthenticated));
        assertThrows(AuthorizationException.class,
                () -> call.accept(unverified));
        assertDoesNotThrow(() -> call.accept(verified));
    }

    @SuppressWarnings("ConstantValue")
    private void testAdminBased(Consumer<Account> call) {
        Account noAuthentication = null;
        Account user = AccountImpl.builder()
                .isAdmin(false).build();
        Account admin = AccountImpl.builder()
                .isAdmin(true).build();

        assertThrows(AuthorizationException.class, () -> call.accept(noAuthentication));
        assertThrows(AuthorizationException.class, () -> call.accept(user));
        assertDoesNotThrow(() -> call.accept(admin));
    }

    @Test
    void requireAccessReplicsWorks() {
        testGroupBased(subject::requireAccessReplics);
    }

    @Test
    void requireCreateReplicsWorks() {
        testGroupBased(subject::requireCreateReplics);
    }

    @Test
    void requireCreateReportsWorks() {
        testGroupBased(subject::requireCreateReports);
    }

    @Test
    void requireAccessAccountsFullWorks() {
        testAdminBased(subject::requireAccessAccountsFull);
    }

    @Test
    void requireChangeServerConfigWorks() {
        testAdminBased(subject::requireChangeServerConfig);
    }

    @Test
    void requireReviewReportsWorks() {
        testAdminBased(subject::requireReviewReports);
    }

    @Test
    void requireAccessReportsWorks() {
        testAdminBased(subject::requireAccessReports);
    }

    @Test
    void requireCreateAccountThrows() {
        when(configService.get()).thenReturn(ServerConfigImpl.builder().allowAccountCreation(false).build());

        Account account = AccountImpl.builder().build();

        assertThrows(AuthorizationException.class,
                () -> subject.requireCreateAccount(account));
    }

    @Test
    void requireCreateAccountAllowsWhenSet() {
        when(configService.get()).thenReturn(ServerConfigImpl.builder().allowAccountCreation(true).build());

        Account account = AccountImpl.builder().build();

        assertDoesNotThrow(() -> subject.requireCreateAccount(account));
    }

    @Test
    void requireUpdateReplicStateAllowsValidOwnerChanges() {
        Set<ReplicState> ownerStates = Set.of(ReplicState.ACTIVE, ReplicState.INACTIVE);
        Account owner = AccountImpl.builder().build();
        Replic replic = ReplicImpl.builder().ownerId(owner.getId()).build();

        for (ReplicState replicState : ownerStates) {
            for (ReplicState newState : ownerStates) {
                replic.setState(replicState);
                assertDoesNotThrow(() -> subject.requireUpdateReplicState(owner, replic, newState));
            }
        }
    }

    @Test
    void requireUpdateReplicStateAllowsValidAdminChanges() {
        Set<ReplicState> adminStates = Set.of(ReplicState.ACTIVE, ReplicState.REMOVED);
        Account owner = AccountImpl.builder().isAdmin(true).build();
        Replic replic = ReplicImpl.builder().build();

        for (ReplicState replicState : adminStates) {
            for (ReplicState newState : adminStates) {
                replic.setState(replicState);
                assertDoesNotThrow(() -> subject.requireUpdateReplicState(owner, replic, newState));
            }
        }
    }

    @Test
    void requireUpdateReplicStateThrowsForInvalidAdminCombos() {
        Set<ReplicState[]> invalidAdminStates = Set.of(new ReplicState[]{ReplicState.ACTIVE, ReplicState.INACTIVE}, new ReplicState[]{ReplicState.INACTIVE, ReplicState.ACTIVE});
        Account admin = AccountImpl.builder().isAdmin(true).build();
        Replic replic = ReplicImpl.builder().build();

        for (ReplicState[] pair : invalidAdminStates) {
            replic.setState(pair[0]);
            assertThrows(AuthorizationException.class,
                    () -> subject.requireUpdateReplicState(admin, replic, pair[1]));
        }
    }

    @Test
    void requireUpdateReplicStateThrowsForInvalidOwnerCombos() {
        Set<ReplicState[]> invalidAdminStates = Set.of(new ReplicState[]{ReplicState.ACTIVE, ReplicState.REMOVED}, new ReplicState[]{ReplicState.REMOVED, ReplicState.ACTIVE});
        Account owner = AccountImpl.builder().build();
        Replic replic = ReplicImpl.builder().ownerId(owner.getId()).build();

        for (ReplicState[] pair : invalidAdminStates) {
            replic.setState(pair[0]);
            assertThrows(AuthorizationException.class,
                    () -> subject.requireUpdateReplicState(owner, replic, pair[1]));
        }
    }

}
