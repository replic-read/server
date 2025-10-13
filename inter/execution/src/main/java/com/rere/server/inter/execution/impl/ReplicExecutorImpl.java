package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.ExpiredException;
import com.rere.server.domain.model.exception.InvalidPasswordException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.FileWriterCallback;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.AuthorizationException;
import com.rere.server.inter.authorization.Authorizer;
import com.rere.server.inter.dto.mapper.EnumMapper;
import com.rere.server.inter.dto.parameter.ReplicSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.request.CreateReplicRequest;
import com.rere.server.inter.dto.response.ReplicResponse;
import com.rere.server.inter.execution.AbstractExecutor;
import com.rere.server.inter.execution.HttpErrorResponseException;
import com.rere.server.inter.execution.ReplicExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Component
public class ReplicExecutorImpl extends AbstractExecutor implements ReplicExecutor<ReplicState, ReplicSortParameter, SortDirectionParameter, UUID, InputStream> {

    private static final String HOST_URL_FORMAT = "%s/replics/%s";

    private final String baseUrl;

    @Autowired
    protected ReplicExecutorImpl(AccountService accountService, AuthenticationService authService, ReplicService replicService, ReportService reportService, ServerConfigService configService, QuotaService quotaService, Authorizer authorizer, @Value("${rere.baseurl}") String baseUrl) {
        super(accountService, authService, replicService, reportService, configService, quotaService, authorizer);
        this.baseUrl = baseUrl;
    }

    private static URL parseUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public List<ReplicResponse> getReplics(ReplicSortParameter sort, SortDirectionParameter direction, UUID replicId, UUID accountId, Set<ReplicState> stateFilter, String query) {
        return replicService.getReplics(getComparatorFromSort(sort, direction), replicId, accountId, stateFilter, query)
                .stream().map(replic -> createReplicResponse(replic, createHostUrl(replic.getId())))
                .toList();
    }

    @Override
    public ReplicResponse createReplic(CreateReplicRequest request, InputStream contentStream) {
        authorizer.requireCreateReplics(getAuth());
        URL url = parseUrl(request.originalUrl());
        MediaMode mediaMode = EnumMapper.mapToEnum(request.mediaMode(), MediaMode.class);
        Instant expiration = request.expiration() != null ? Instant.parse(request.expiration()) : null;

        FileWriterCallback writeToFile = file -> {
            try {
                contentStream.transferTo(new FileOutputStream(file));
                return true;
            } catch (IOException e) {
                return false;
            }
        };

        Replic created;
        try {
            created = replicService.createReplic(url, mediaMode, request.description(), expiration, request.password(), getAuth(), writeToFile);
        } catch (DomainException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        }

        return createReplicResponse(created, createHostUrl(created.getId()));
    }

    @Override
    public String getReplicContent(UUID id, String password) {
        authorizer.requireAccessReplics(getAuth());
        InputStream contentStream;
        try {
            contentStream = replicService.receiveContent(id, password);
        } catch (NotFoundException | InvalidPasswordException e) {
            throw AuthorizationException.genericForbidden();
        } catch (ExpiredException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        }

        InputStreamReader reader = new InputStreamReader(contentStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        return bufferedReader.lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public void updateReplicState(UUID id, ReplicState state) {
        Replic replic = replicService.getReplicById(id)
                .orElseThrow(AuthorizationException::genericForbidden);
        authorizer.requireUpdateReplicState(getAuth(), replic, state);

        try {
            replicService.setReplicState(id, state);
        } catch (NotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private String createHostUrl(UUID replicId) {
        return HOST_URL_FORMAT.formatted(baseUrl, replicId.toString());
    }
}
