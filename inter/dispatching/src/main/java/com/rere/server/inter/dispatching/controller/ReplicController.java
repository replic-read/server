package com.rere.server.inter.dispatching.controller;

import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.inter.dispatching.documentation.endpoint.EndpointMetadata;
import com.rere.server.inter.dto.error.domain.MessageBasedInfo;
import com.rere.server.inter.dto.parameter.ReplicSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.request.CreateReplicRequest;
import com.rere.server.inter.dto.response.ReplicResponse;
import com.rere.server.inter.dto.validation.ValidationMetadata;
import com.rere.server.inter.execution.HttpErrorResponseException;
import com.rere.server.inter.execution.ReplicExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.BAD_AUTHENTICATION;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.CREATE_REPLIC_BAD_EXPIRATION;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.CREATE_REPLIC_QUOTA_REACHED;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.NO_PERMISSION_NO_EXIST;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.SUCCESS;
import static com.rere.server.inter.dispatching.documentation.endpoint.AuthorizationType.CONFIG_SPECIFIC;
import static com.rere.server.inter.dispatching.documentation.endpoint.AuthorizationType.REPLIC_STATUS_UPDATE;
import static com.rere.server.inter.dto.mapper.EnumMapper.mapToEnum;
import static com.rere.server.inter.dto.validation.FieldType.FILTER_QUERY;
import static com.rere.server.inter.dto.validation.FieldType.REPLIC_FILTER_STATE;
import static com.rere.server.inter.dto.validation.FieldType.REPLIC_FILTER_USER;
import static com.rere.server.inter.dto.validation.FieldType.REPLIC_ID;
import static com.rere.server.inter.dto.validation.FieldType.REPLIC_PASSWORD;
import static com.rere.server.inter.dto.validation.FieldType.REPLIC_SORT;
import static com.rere.server.inter.dto.validation.FieldType.REPLIC_STATE;
import static com.rere.server.inter.dto.validation.FieldType.SORT_DIRECTION;

/**
 * The web-controller for replic matters.
 */
@Tag(
        name = "Replics",
        description = "Handles the different actions related to eplics."
)
@RestController
@RequestMapping("/replics")
public class ReplicController {

    private final ReplicExecutor<ReplicState, ReplicSortParameter, SortDirectionParameter, UUID, InputStream> executor;

    @Autowired
    public ReplicController(ReplicExecutor<ReplicState, ReplicSortParameter, SortDirectionParameter, UUID, InputStream> executor) {
        this.executor = executor;
    }

    @Operation(
            summary = "All replics",
            description = "Gets all replics.<br><br>The replics that are returned depend on whether an admin is making the request, or not. For example, removed replics are not returned to normal users."
    )
    @EndpointMetadata(
            authorizationType = CONFIG_SPECIFIC,
            responseTypes = {SUCCESS,
                    NO_PERMISSION_NO_EXIST,
                    BAD_AUTHENTICATION}
    )
    @GetMapping("/")
    public List<ReplicResponse> getReplics(
            @ValidationMetadata(value = REPLIC_SORT, required = false) @Valid @RequestParam(name = "sort", required = false) String sortMode,
            @ValidationMetadata(value = SORT_DIRECTION, required = false) @Valid @RequestParam(name = "direction", required = false) String sortDirection,
            @ValidationMetadata(value = REPLIC_FILTER_USER, required = false) @Valid @RequestParam(name = "user", required = false) String userId,
            @ValidationMetadata(value = REPLIC_ID, required = false) @Valid @RequestParam(name = "replic_id", required = false) String replicId,
            @ValidationMetadata(value = REPLIC_FILTER_STATE, required = false) @Valid @RequestParam(name = "filter", required = false) Set<String> stateFilter,
            @ValidationMetadata(value = FILTER_QUERY, required = false) @Valid @RequestParam(name = "query", required = false) String query

    ) {
        return executor.getReplics(
                mapToEnum(sortMode, ReplicSortParameter.class),
                mapToEnum(sortDirection, SortDirectionParameter.class),
                replicId != null ? UUID.fromString(replicId) : null,
                userId != null ? UUID.fromString(userId) : null,
                stateFilter.stream().map(state -> mapToEnum(state, ReplicState.class)).collect(Collectors.toSet()),
                query
        );
    }

    @Operation(
            summary = "Create a replic",
            description = "Creates a new replic."
    )
    @EndpointMetadata(
            authorizationType = CONFIG_SPECIFIC,
            responseTypes = {CREATE_REPLIC_BAD_EXPIRATION,
                    CREATE_REPLIC_QUOTA_REACHED,
                    SUCCESS,
                    NO_PERMISSION_NO_EXIST,
                    BAD_AUTHENTICATION}
    )
    @PostMapping("/")
    public ReplicResponse createReplic(
            @Valid @RequestPart CreateReplicRequest requestBody,
            @RequestPart MultipartFile file
    ) {
        try {
            return executor.createReplic(requestBody, file.getInputStream());
        } catch (IOException e) {
            throw new HttpErrorResponseException(new MessageBasedInfo("An error occurred reading the file content"), 500);
        }
    }

    @Operation(
            summary = "Get content of specific replic",
            description = "Gets the replicated HTML-content of a specific replic."
    )
    @EndpointMetadata(
            authorizationType = CONFIG_SPECIFIC,
            responseTypes = {SUCCESS,
                    NO_PERMISSION_NO_EXIST,
                    BAD_AUTHENTICATION}
    )
    @GetMapping("/{id}/content/")
    public String getReplicContent(
            @ValidationMetadata(REPLIC_ID) @Valid @PathVariable(name = "id") String id,
            @ValidationMetadata(value = REPLIC_PASSWORD, required = false) @RequestParam(name = "password", required = false) String password
    ) {
        return executor.getReplicContent(
                UUID.fromString(id),
                password
        );
    }

    @Operation(
            summary = "Update the state of a replic",
            description = "Updates the state of a replic."
    )
    @EndpointMetadata(
            authorizationType = REPLIC_STATUS_UPDATE,
            responseTypes = {SUCCESS,
                    NO_PERMISSION_NO_EXIST,
                    BAD_AUTHENTICATION}
    )
    @PutMapping("/{id}/")
    public void updateReplicState(
            @ValidationMetadata(REPLIC_ID) @Valid @PathVariable String id,
            @ValidationMetadata(REPLIC_STATE) @Valid @RequestParam String state
    ) {
        executor.updateReplicState(UUID.fromString(id), mapToEnum(state, ReplicState.class));
    }
}
