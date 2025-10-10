package com.rere.server.inter.execution.dto.response;

/**
 * Response body that has information about a report.
 */
public record ReportResponse(String id, String createdTimestamp, String userId, String replicId) {

}
