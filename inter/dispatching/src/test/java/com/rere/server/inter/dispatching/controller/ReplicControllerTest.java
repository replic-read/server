package com.rere.server.inter.dispatching.controller;

import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.inter.dto.parameter.ReplicSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.response.ReplicResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReplicControllerTest extends AbstractMvcTest {

    @Test
    void getReplicsCallsExecutorAndReturns() throws Exception {
        when(replicExecutor.getReplics(any(), any(), any(), any(), any(), any()))
                .thenReturn(IntStream.range(0, 5).mapToObj(i ->
                        new ReplicResponse(UUID.randomUUID().toString(), Instant.now().toString(), "desc" + i,
                                "removed", "https://google.com/", 111, "https://example.com/replics/sss",
                                null, null, "none", false)
                ).toList());

        UUID userId = UUID.randomUUID();
        UUID replicId = UUID.randomUUID();
        client.perform(get("/replics/")
                        .queryParam("sort", "size")
                        .queryParam("direction", "descending")
                        .queryParam("user", userId.toString())
                        .queryParam("replic_id", replicId.toString())
                        .queryParam("filter", "active", "removed")
                        .queryParam("query", "<blahh>"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].description").value("desc0"))
                .andExpect(jsonPath("$[3].size").value(111));

        var sortCaptor = ArgumentCaptor.<ReplicSortParameter>captor();
        var directionCaptor = ArgumentCaptor.<SortDirectionParameter>captor();
        var replicIdCaptor = ArgumentCaptor.<UUID>captor();
        var accountIdCaptor = ArgumentCaptor.<UUID>captor();
        var stateFilterCaptor = ArgumentCaptor.<Set<ReplicState>>captor();
        var queryCaptor = ArgumentCaptor.<String>captor();

        verify(replicExecutor).getReplics(sortCaptor.capture(), directionCaptor.capture(), replicIdCaptor.capture(),
                accountIdCaptor.capture(), stateFilterCaptor.capture(), queryCaptor.capture());

        assertEquals(ReplicSortParameter.SIZE, sortCaptor.getValue());
        assertEquals(SortDirectionParameter.DESCENDING, directionCaptor.getValue());
        assertEquals(replicId, replicIdCaptor.getValue());
        assertEquals(userId, accountIdCaptor.getValue());
        assertEquals(Set.of(ReplicState.ACTIVE, ReplicState.REMOVED), stateFilterCaptor.getValue());
        assertEquals("<blahh>", queryCaptor.getValue());
    }

    @Test
    void createReplicCallsExecutorAndReturns() throws Exception {
        when(replicExecutor.createReplic(any(), any()))
                .thenReturn(new ReplicResponse(UUID.randomUUID().toString(), Instant.now().toString(), "desc",
                        "removed", "https://google.com/", 111, "https://example.com/replics/sss",
                        null, null, "none", false));

        String html = "<h1>Hello world</h1>";
        MockMultipartFile mockFile = new MockMultipartFile("file", html.getBytes(StandardCharsets.UTF_8));

        String body = """
                {
                "original_url": "https://google.com",
                "media_mode": "images",
                "expiration": "%s",
                "password": null
                }
                """.formatted(Instant.now().toString());
        MockMultipartFile bodyFile = new MockMultipartFile("request_body", "", "application/json", body.getBytes(StandardCharsets.UTF_8));

        client.perform(multipart(HttpMethod.POST, "/replics/")
                        .file(mockFile)
                        .file(bodyFile)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.host_url").value("https://example.com/replics/sss"));
    }

    @Test
    void getReplicContentCallsExecutorAndReturns() throws Exception {
        InputStream content = new ByteArrayInputStream("<h1>Hello world!</h1>".getBytes(StandardCharsets.UTF_8));
        when(replicExecutor.getReplicContent(any(), any()))
                .thenReturn(content);

        UUID replicId = UUID.randomUUID();

        setupAuth();
        client.perform(get("/replics/%s/content/".formatted(replicId)))
                .andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andExpect(status().isOk())
                .andExpect(content().string("<h1>Hello world!</h1>"));

        var replicIdCaptor = ArgumentCaptor.<UUID>captor();

        verify(replicExecutor).getReplicContent(replicIdCaptor.capture(), any());

        assertEquals(replicId, replicIdCaptor.getValue());
    }

    @Test
    void updateReplicStateFailsForNoAuth() throws Exception {
        UUID replicId = UUID.randomUUID();
        assertForbidden(put("/replics/%s/".formatted(replicId)));
    }

    @Test
    void updateReplicStateCallsExecutorAndReturns() throws Exception {
        UUID replicId = UUID.randomUUID();
        setupAuth();
        client.perform(put("/replics/%s/".formatted(replicId))
                        .queryParam("state", "removed"))
                .andExpect(status().isOk());

        var replicIdCaptor = ArgumentCaptor.<UUID>captor();
        verify(replicExecutor).updateReplicState(replicIdCaptor.capture(), any());
        assertEquals(replicId, replicIdCaptor.getValue());
    }

}