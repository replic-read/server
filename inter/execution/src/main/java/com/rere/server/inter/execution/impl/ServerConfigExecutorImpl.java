package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.impl.ReplicLimitConfigImpl;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.Authorizer;
import com.rere.server.inter.dto.request.ServerConfigRequest;
import com.rere.server.inter.dto.response.ServerConfigResponse;
import com.rere.server.inter.execution.AbstractExecutor;
import com.rere.server.inter.execution.ServerConfigExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Period;

import static com.rere.server.inter.dto.mapper.EnumMapper.mapToEnum;
import static com.rere.server.inter.dto.mapper.EnumMapper.mapToString;

@Primary
@Component
public class ServerConfigExecutorImpl extends AbstractExecutor implements ServerConfigExecutor {
    @Autowired
    protected ServerConfigExecutorImpl(AccountService accountService, AuthenticationService authService, ReplicService replicService, ReportService reportService, ServerConfigService configService, QuotaService quotaService, Authorizer authorizer, @Value("${rere.baseurl") String baseUrl) {
        super(accountService, authService, replicService, reportService, configService, quotaService, authorizer);
    }

    private static ServerConfigResponse createServerConfigResponse(ServerConfig config) {
        return new ServerConfigResponse(mapToString(config.getCreateReplicsGroup()), mapToString(config.getAccessReplicsGroup()),
                mapToString(config.getCreateReportsGroup()),
                config.getMaximumActivePeriod() != null ? config.getMaximumActivePeriod().toString() : null,
                config.getLimit() != null ? config.getLimit().getPeriod().toString() : null,
                config.getLimit() != null ? config.getLimit().getCount() : null,
                config.getLimit() != null ? config.getLimit().getPeriodStart().toString() : null,
                config.isAllowAccountCreation());
    }

    @Override
    public ServerConfigResponse getServerConfig() {
        return createServerConfigResponse(configService.get());
    }

    @Override
    public ServerConfigResponse setServerConfig(ServerConfigRequest request) {
        authorizer.requireChangeServerConfig(getAuth());

        ReplicLimitConfig newLimit = (request.replicLimitCount() != null &&
                                      request.replicLimitPeriod() != null)
                ? new ReplicLimitConfigImpl(Period.parse(request.replicLimitPeriod()),
                null, request.replicLimitCount()) : null;
        ServerConfig newConfig = new ServerConfigImpl(
                mapToEnum(request.createReplicGroup(), AuthUserGroup.class),
                mapToEnum(request.accessReplicGroup(), AuthUserGroup.class),
                mapToEnum(request.createReportGroup(), AuthUserGroup.class),
                request.allowSignup(),
                newLimit,
                request.maximumExpirationPeriod() != null ? Period.parse(request.maximumExpirationPeriod()) : null
        );
        configService.save(newConfig);

        return createServerConfigResponse(configService.get());
    }
}
