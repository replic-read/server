package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.ReplicContentWriteException;
import com.rere.server.domain.model.impl.ReplicImpl;
import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicState;
import com.rere.server.domain.service.FileWriterCallback;
import com.rere.server.inter.authorization.AuthorizationException;
import com.rere.server.inter.dto.request.CreateReplicRequest;
import com.rere.server.inter.dto.response.ReplicResponse;
import com.rere.server.inter.execution.error.HttpErrorResponseException;
import com.rere.server.inter.execution.parameter.ReplicSortParameter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReplicExecutorImplTest extends BaseExecutorTest {

    @InjectMocks
    private ReplicExecutorImpl subject;

    private static InputStream isForBytes(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void getReplicsDelegatesToService() {
        List<Replic> replics = IntStream.range(0, 10)
                .mapToObj(i -> (Replic) ReplicImpl.builder().build())
                .toList();

        when(replicService.getReplics(any(), any(), any(), any(), any()))
                .thenReturn(replics);

        List<ReplicResponse> response = subject.getReplics(ReplicSortParameter.DATE, null, null, null, null, null);

        assertEquals(10, response.size());
    }

    @Test
    void createReplicPropagatesAuthorization() {
        assertAuthorizationIsPropagated(() -> subject.createReplic(
                new CreateReplicRequest("https://google.com", "all", null, null, null),
                isForBytes("test")
        ));
    }

    @Test
    void createReplicWritesToFile() throws DomainException, IOException {
        when(replicService.createReplic(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ReplicImpl.builder().build());

        subject.createReplic(new CreateReplicRequest("https://google.com", "all", null, null, null),
                isForBytes("test"));

        ArgumentCaptor<URL> urlCaptor = ArgumentCaptor.captor();
        ArgumentCaptor<MediaMode> mediaModeCaptor = ArgumentCaptor.captor();
        ArgumentCaptor<FileWriterCallback> callbackCaptor = ArgumentCaptor.captor();

        verify(replicService).createReplic(urlCaptor.capture(), mediaModeCaptor.capture(), any(), any(), any(), any(), callbackCaptor.capture());

        assertEquals("https://google.com", urlCaptor.getValue().toString());
        assertEquals(MediaMode.ALL, mediaModeCaptor.getValue());

        // Testing if the file writer callback works as expected
        File temp = File.createTempFile(getClass().getPackage().toString(), ".txt");
        temp.createNewFile();
        callbackCaptor.getValue().write(temp);

        List<String> lines = Files.readAllLines(temp.toPath());
        assertEquals(1, lines.size());
        assertEquals("test", lines.getFirst());
    }

    @Test
    void createReplicConvertsDomainException() throws DomainException {
        when(replicService.createReplic(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new ReplicContentWriteException()); //Placeholder for any DomainException

        assertThrows(HttpErrorResponseException.class,
                () -> subject.createReplic(new CreateReplicRequest("https://google.com", "all", null, null, null),
                        isForBytes("test")));
    }

    @Test
    void getReplicContentPropagatesAuthorization() {
        assertAuthorizationIsPropagated(() -> subject.getReplicContent(UUID.randomUUID(), null));
    }

    @Test
    void getReplicContentReadsStreamCorrect() throws DomainException {
        when(replicService.receiveContent(any(), any()))
                .thenReturn(isForBytes("the universe, life and everything"));

        String returned = subject.getReplicContent(UUID.randomUUID(), null);

        assertEquals("the universe, life and everything", returned);
    }

    @Test
    void getReplicContentConvertsException() throws DomainException {
        when(replicService.receiveContent(any(), any()))
                .thenThrow(NotFoundException.replic(UUID.randomUUID()));

        assertThrows(AuthorizationException.class,
                () -> subject.getReplicContent(UUID.randomUUID(), null));
    }

    @Test
    void updateReplicStatePropagatesAuthorization() {
        when(replicService.getReplicById(any()))
                .thenReturn(Optional.of(ReplicImpl.builder().build()));

        assertAuthorizationIsPropagated(() -> subject.updateReplicState(UUID.randomUUID(), null));
    }

    @Test
    void updateReplicStateCallsService() throws NotFoundException {
        when(replicService.getReplicById(any()))
                .thenReturn(Optional.of(ReplicImpl.builder().build()));
        when(replicService.setReplicState(any(), any()))
                .thenReturn(ReplicImpl.builder().build());

        subject.updateReplicState(UUID.randomUUID(), ReplicState.ACTIVE);

        verify(replicService, times(1)).setReplicState(any(), eq(ReplicState.ACTIVE));
    }

    @Test
    void updateReplicStateConvertsNotFound() throws NotFoundException {
        when(replicService.getReplicById(any()))
                .thenReturn(Optional.of(ReplicImpl.builder().build()));
        when(replicService.setReplicState(any(), any()))
                .thenThrow(NotFoundException.replic(UUID.randomUUID()));

        assertThrows(IllegalStateException.class,
                () -> subject.updateReplicState(UUID.randomUUID(), ReplicState.ACTIVE));
    }

}