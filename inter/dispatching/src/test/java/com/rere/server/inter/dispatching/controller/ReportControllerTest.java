package com.rere.server.inter.dispatching.controller;

import com.rere.server.inter.dto.parameter.ReportSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.response.ReportResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerTest extends AbstractControllerTest {

    @Test
    void getReportsFailsForNoAuth() throws Exception {
        UUID reportId = UUID.randomUUID();
        assertForbidden(get("/reports/")
                .queryParam("sort", "user")
                .queryParam("direction", "ascending")
                .queryParam("report_id", reportId.toString())
                .queryParam("query", "<mock-query>"));
    }

    @Test
    void getReportsCallsExecutorAndReturns() throws Exception {
        when(reportExecutor.getReports(any(), any(), any(), any()))
                .thenReturn(IntStream.range(0, 7).mapToObj(i ->
                        new ReportResponse(UUID.randomUUID().toString(), Instant.now().toString(),
                                null, UUID.randomUUID().toString())
                ).toList());

        UUID reportId = UUID.randomUUID();
        setupAuth();
        client.perform(get("/reports/")
                        .queryParam("sort", "user")
                        .queryParam("direction", "ascending")
                        .queryParam("report_id", reportId.toString())
                        .queryParam("query", "<mock-query>"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7));

        var sortCaptor = ArgumentCaptor.<ReportSortParameter>captor();
        var directionCaptor = ArgumentCaptor.<SortDirectionParameter>captor();
        var reportIdCaptor = ArgumentCaptor.<UUID>captor();
        var queryCaptor = ArgumentCaptor.<String>captor();

        verify(reportExecutor).getReports(sortCaptor.capture(), directionCaptor.capture(), reportIdCaptor.capture(), queryCaptor.capture());

        assertEquals(ReportSortParameter.USER, sortCaptor.getValue());
        assertEquals(SortDirectionParameter.ASCENDING, directionCaptor.getValue());
        assertEquals(reportId, reportIdCaptor.getValue());
        assertEquals("<mock-query>", queryCaptor.getValue());
    }

    @Test
    void createReportCallsExecutorAndReturns() throws Exception {
        UUID reportId = UUID.randomUUID();
        UUID replicId = UUID.randomUUID();
        when(reportExecutor.createReport(any(), any()))
                .thenReturn(new ReportResponse(reportId.toString(), Instant.now().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()));

        String content = """
                {
                "description": null
                }
                """;

        client.perform(post("/reports/").content(content)
                        .queryParam("replic_id", replicId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reportId.toString()));
    }

    @Test
    void updateReportFailsForNoAuth() throws Exception {
        UUID reportId = UUID.randomUUID();
        assertForbidden(put("/reports/%s/".formatted(reportId))
                .queryParam("state", "open"));
    }

    @Test
    void updateReportCallsExecutorAndReturns() throws Exception {
        UUID reportId = UUID.randomUUID();
        when(reportExecutor.updateReport(any(), any()))
                .thenReturn(new ReportResponse(reportId.toString(), Instant.now().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()));

        setupAuth();
        client.perform(put("/reports/%s/".formatted(reportId))
                        .queryParam("state", "open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reportId.toString()));
    }

}