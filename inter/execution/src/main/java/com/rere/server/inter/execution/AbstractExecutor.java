package com.rere.server.inter.execution;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.report.Report;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.Authorizer;
import com.rere.server.inter.execution.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.execution.dto.parameter.SortParameter;
import com.rere.server.inter.execution.dto.response.AccountResponse;
import com.rere.server.inter.execution.dto.response.PartialAccountResponse;
import com.rere.server.inter.execution.dto.response.ReplicResponse;
import com.rere.server.inter.execution.dto.response.ReportResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

import static com.rere.server.inter.execution.mapper.EnumMapper.mapToString;

/**
 * Abstract base class for the executors.
 * Provides utility methods and services.
 */
public class AbstractExecutor {


    /**
     * The account service.
     */
    protected final AccountService accountService;
    /**
     * The authentication service.
     */
    protected final AuthenticationService authService;
    /**
     * The replic service.
     */
    protected final ReplicService replicService;
    /**
     * The report service.
     */
    protected final ReportService reportService;
    /**
     * The config service.
     */
    protected final ServerConfigService configService;
    /**
     * The quota service.
     */
    protected final QuotaService quotaService;
    /**
     * The authorizer.
     */
    protected final Authorizer authorizer;
    /**
     * The authentication of the client that is currently being handled.
     */
    @Getter
    @Setter
    private Account auth;

    protected AbstractExecutor(AccountService accountService, AuthenticationService authService, ReplicService replicService, ReportService reportService, ServerConfigService configService, QuotaService quotaService, Authorizer authorizer) {
        this.accountService = accountService;
        this.authService = authService;
        this.replicService = replicService;
        this.reportService = reportService;
        this.configService = configService;
        this.quotaService = quotaService;
        this.authorizer = authorizer;
    }

    /**
     * Creates an AccountResponse from a given Account.
     * @param account The Account.
     * @return The AccountResponse.
     */
    protected AccountResponse createAccountResponse(Account account) {
        return new AccountResponse(account.getId().toString(), account.getCreationTimestamp().toString(),
                account.getEmail(), account.getUsername(), account.getProfileColor(),
                mapToString(account.getAccountState()));
    }

    /**
     * Creates a PartialAccountResponse from a given Account.
     * @param account The Account.
     * @return The PartialAccountResponse.
     */
    protected PartialAccountResponse createAccountResponsePartial(Account account) {
        return new PartialAccountResponse(account.getUsername(), account.getProfileColor(), mapToString(account.getAccountState()));
    }

    /**
     * Creates a ReplicResponse from a given Replic.
     * @param r The replic.
     * @return The ReplicResponse.
     */
    protected ReplicResponse createReplicResponse(Replic r, String hostUrl) {
        return new ReplicResponse(r.getId().toString(), r.getCreationTimestamp().toString(),
                r.getDescription(), mapToString(r.getState()), r.getOriginalUrl().toString(), r.getSize(),
                hostUrl, r.getExpirationTimestamp() != null ? r.getExpirationTimestamp().toString() : null,
                r.getOwnerId() != null ? r.getOwnerId().toString() : null, mapToString(r.getMediaMode()),
                r.getPasswordHash() != null);
    }

    /**
     * Creates a ReportResponse from a given Report.
     * @param r The report.
     * @return The response.
     */
    protected ReportResponse createReportResponse(Report r) {
        return new ReportResponse(r.getId().toString(), r.getCreationTimestamp().toString(),
                r.getAuthorId() != null ? r.getAuthorId().toString() : null, r.getReplicId().toString());
    }

    /**
     * Gets the comparator from a sort parameter.
     * @param param The sort parameter.
     * @param direction The direction.
     * @return The comparator.
     * @param <T> The type of parameter.
     */
    protected <T> Comparator<T> getComparatorFromSort(SortParameter<T> param, SortDirectionParameter direction) {
        return param != null ? param.getComparator(direction) : null;
    }
}
