package com.rere.server.inter.authorization.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.AuthorizationException;
import com.rere.server.inter.authorization.Authorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Implementation of the authorizer based on the admin roles and groups.
 */
@Component
public class RoleGroupBasedAuthorizer implements Authorizer {

    private final ServerConfigService configService;

    @Autowired
    public RoleGroupBasedAuthorizer(ServerConfigService configService) {
        this.configService = configService;
    }

    private static void requireGroup(Account account, AuthUserGroup group) {
        boolean hasAccess = switch (group) {
            case ALL -> true;
            case ACCOUNT -> account != null;
            case VERIFIED -> account != null && account.getAccountState().equals(AccountState.ACTIVE);
        };

        if (!hasAccess) {
            throw AuthorizationException.onlyGroup(group);
        }
    }

    private static void requireAdmin(Account account) {
        if (account == null || !account.isAdmin()) {
            throw AuthorizationException.onlyAdmins();
        }
    }

    @Override
    public void requireAccessReplics(Account account) {
        requireGroup(account, configService.get().getAccessReplicsGroup());
    }

    @Override
    public void requireCreateReplics(Account account) {
        requireGroup(account, configService.get().getCreateReplicsGroup());
    }

    @Override
    public void requireCreateAccount(Account account) {
        if (!configService.get().isAllowAccountCreation()) {
            throw AuthorizationException.disabledByConfig();
        }
    }

    @Override
    public void requireAccessAccountsFull(Account account) {
        requireAdmin(account);
    }

    @Override
    public void requireChangeServerConfig(Account account) {
        requireAdmin(account);
    }

    @Override
    public void requireReviewReports(Account account) {
        requireAdmin(account);
    }

    @Override
    public void requireAccessReports(Account account) {
        requireAdmin(account);
    }

    @Override
    public void requireCreateReports(Account account) {
        requireGroup(account, configService.get().getCreateReportsGroup());
    }

    @Override
    public void requireUpdateReplicState(Account account, Replic replic, ReplicState state) {
        boolean isOwner = replic.getOwnerId() != null && replic.getOwnerId().equals(account != null ? account.getId() : null);
        boolean isAdmin = account != null && account.isAdmin();

        Set<ReplicState> ownerStates = Set.of(ReplicState.ACTIVE, ReplicState.INACTIVE);
        boolean validChangeForOwner = ownerStates.contains(replic.getState()) &&
                                      ownerStates.contains(state) &&
                                      isOwner;
        Set<ReplicState> adminStates = Set.of(ReplicState.ACTIVE, ReplicState.REMOVED);
        boolean validChangeForAdmin = adminStates.contains(replic.getState()) &&
                                      adminStates.contains(state) &&
                                      isAdmin;

        boolean valid = validChangeForOwner || validChangeForAdmin;
        if (!valid) {
            throw AuthorizationException.genericForbidden();
        }
    }
}
