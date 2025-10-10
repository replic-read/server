package com.rere.server.inter.execution;

import com.rere.server.inter.execution.dto.request.CreateReportRequest;
import com.rere.server.inter.execution.dto.response.ReportResponse;

import java.util.List;

/**
 * Executor that handles requests for reports.
 */
public interface ReportExecutor<RS, RSP, D, I> {

    /**
     * Executor for GET /reports/.
     * @param sort The 'sort' query parameter.
     * @param direction The 'direction' query parameter.
     * @param reportId The 'report_id' query parameter.
     * @param query The 'query' query parameter.
     * @return The response body.
     */
    List<ReportResponse> getReports(RSP sort, D direction, I reportId, String query);

    /**
     * Executor for POST /reports/.
     * @param request The request body.
     * @param replicId The 'replic_id' query parameter.
     * @return The response body.
     */
    ReportResponse createReport(CreateReportRequest request, I replicId);

    /**
     * Executor for PUT /reports/{id}/.
     * @param reportId The 'id' path variable.
     * @param state The 'state' query parameter.
     * @return The response body.
     */
    ReportResponse updateReport(I reportId, RS state);

}
