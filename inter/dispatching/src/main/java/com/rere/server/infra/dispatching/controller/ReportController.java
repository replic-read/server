package com.rere.server.infra.dispatching.controller;

import com.rere.server.domain.model.report.ReportState;
import com.rere.server.infra.dispatching.documentation.endpoint.EndpointMetadata;
import com.rere.server.inter.dto.parameter.ReportSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.request.CreateReportRequest;
import com.rere.server.inter.dto.response.ReportResponse;
import com.rere.server.inter.dto.validation.ValidationMetadata;
import com.rere.server.inter.execution.ReportExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.rere.server.infra.dispatching.documentation.endpoint.ApiResponseType.BAD_AUTHENTICATION;
import static com.rere.server.infra.dispatching.documentation.endpoint.ApiResponseType.NO_PERMISSION_NO_EXIST;
import static com.rere.server.infra.dispatching.documentation.endpoint.ApiResponseType.SUCCESS;
import static com.rere.server.infra.dispatching.documentation.endpoint.AuthorizationType.ADMIN;
import static com.rere.server.infra.dispatching.documentation.endpoint.AuthorizationType.CONFIG_SPECIFIC;
import static com.rere.server.inter.dto.mapper.EnumMapper.mapToEnum;
import static com.rere.server.inter.dto.validation.FieldType.FILTER_QUERY;
import static com.rere.server.inter.dto.validation.FieldType.REPLIC_ID;
import static com.rere.server.inter.dto.validation.FieldType.REPORT_ID;
import static com.rere.server.inter.dto.validation.FieldType.REPORT_SORT;
import static com.rere.server.inter.dto.validation.FieldType.REPORT_STATE;
import static com.rere.server.inter.dto.validation.FieldType.SORT_DIRECTION;

/**
 * The web-controller for report matters.
 * <br>
 * Implements ReportExecutor as semantic detail.
 * We don't need the polymorphism, but as this class acts as a proxy, it makes sense to implement the interface.
 */
@Tag(
        name = "Reports",
        description = "Handles the different actions related to reports."
)
@RestController
@RequestMapping("/reports")
public class ReportController implements ReportExecutor<String, String, String, String> {

    private final ReportExecutor<ReportState, ReportSortParameter, SortDirectionParameter, UUID> executor;

    @Autowired
    public ReportController(ReportExecutor<ReportState, ReportSortParameter, SortDirectionParameter, UUID> executor) {
        this.executor = executor;
    }

    @Operation(
            summary = "All reports",
            description = "Gets all reports that have not been reviewed or closed."
    )
    @EndpointMetadata(
            authorizationType = ADMIN,
            responseTypes = {SUCCESS,
                    NO_PERMISSION_NO_EXIST,
                    BAD_AUTHENTICATION}
    )
    @GetMapping("/")
    @Override
    public List<ReportResponse> getReports(
            @ValidationMetadata(value = REPORT_SORT, required = false) @Valid @RequestParam(name = "sort", required = false) String sortMode,
            @ValidationMetadata(value = SORT_DIRECTION, required = false) @Valid @RequestParam(name = "direction", required = false) String sortDirection,
            @ValidationMetadata(value = REPORT_ID, required = false) @Valid @RequestParam(name = "report_id", required = false) String reportId,
            @ValidationMetadata(value = FILTER_QUERY, required = false) @Valid @RequestParam(name = "query", required = false) String query
    ) {
        return executor.getReports(
                mapToEnum(sortMode, ReportSortParameter.class),
                mapToEnum(sortDirection, SortDirectionParameter.class),
                reportId != null ? UUID.fromString(reportId) : null,
                query
        );
    }

    @Operation(
            summary = "Create a report",
            description = "Creates a new report for the specific replic."
    )
    @EndpointMetadata(
            authorizationType = CONFIG_SPECIFIC,
            responseTypes = {SUCCESS,
                    BAD_AUTHENTICATION,
                    NO_PERMISSION_NO_EXIST}
    )
    @PostMapping("/")
    @Override
    public ReportResponse createReport(
            @Valid @RequestBody CreateReportRequest requestBody,
            @ValidationMetadata(REPLIC_ID) @Valid @RequestParam(name = "replic_id") String replicId
    ) {
        return executor.createReport(requestBody, UUID.fromString(replicId));
    }

    @Operation(
            summary = "Change report status",
            description = "Changes the status of a report."
    )
    @EndpointMetadata(
            authorizationType = ADMIN,
            responseTypes = {SUCCESS,
                    BAD_AUTHENTICATION,
                    NO_PERMISSION_NO_EXIST}
    )
    @PutMapping("/{id}/")
    @Override
    public ReportResponse updateReport(
            @ValidationMetadata(REPORT_ID) @Valid @PathVariable String id,
            @ValidationMetadata(REPORT_STATE) @Valid @RequestParam String state
    ) {
        return executor.updateReport(UUID.fromString(id), mapToEnum(state, ReportState.class));
    }
}
