package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotFoundSubject;
import com.rere.server.domain.model.impl.ReplicImpl;
import com.rere.server.domain.model.impl.ReportImpl;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.report.Report;
import com.rere.server.domain.repository.ReportRepository;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.BaseDomainServiceTest;
import com.rere.server.domain.service.ReplicService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link ReportServiceImpl} class.
 */
class ReportServiceImplTest extends BaseDomainServiceTest {

    @Mock
    private ReportRepository reportRepo;

    @Mock
    private ReplicService replicService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private ReportServiceImpl subject;

    @Test
    void getReportsSorts() {
        List<UUID> idList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            idList.add(UUID.randomUUID());
        }

        List<UUID> shuffledIds = new ArrayList<>(idList);
        Collections.shuffle(shuffledIds);

        List<Report> reports = new ArrayList<>();
        for (UUID id : shuffledIds) {
            reports.add(ReportImpl.builder().id(id).build());
        }

        when(reportRepo.getAll()).thenReturn(reports);

        Comparator<Report> comparator = Comparator.comparingInt(c -> idList.indexOf(c.getId()));

        List<Report> returned = subject.getReports(comparator, null);

        for (int i = 0; i < idList.size(); i++) {
            assertEquals(idList.get(i), returned.get(i).getId());
        }
    }

    @Test
    void getReportsFiltersQuery() {
        List<Report> reports = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            reports.add(ReportImpl.builder().build());
        }
        Report specialReport = ReportImpl.builder().description("fdsfsfdsfthe universe, life and everythingfsfsdfsfd").build();
        reports.add(specialReport);

        when(reportRepo.getAll()).thenReturn(reports);

        List<Report> returned = subject.getReports(null, "the universe, life and everything");

        assertEquals(1, returned.size());
        assertEquals(specialReport, returned.getFirst());
    }

    @Test
    void reportReplicFailsForNotFindReplicOrAccount() {
        UUID replicNotExistId = UUID.randomUUID();
        UUID accountNotExistId = UUID.randomUUID();

        Replic replic = ReplicImpl.builder().build();

        when(replicService.getReplicById(replic.getId())).thenReturn(Optional.of(replic));
        when(replicService.getReplicById(replicNotExistId)).thenReturn(Optional.empty());
        when(accountService.getAccountById(accountNotExistId)).thenReturn(Optional.empty());

        NotFoundException ex1 = assertThrows(NotFoundException.class,
                () -> subject.reportReplic(replicNotExistId, accountNotExistId, null));
        assertEquals(NotFoundSubject.REPLIC, ex1.getSubject());
        assertEquals(replicNotExistId, ex1.getIdentifier());

        NotFoundException ex2 = assertThrows(NotFoundException.class,
                () -> subject.reportReplic(replic.getId(), accountNotExistId, null));
        assertEquals(NotFoundSubject.ACCOUNT, ex2.getSubject());
        assertEquals(accountNotExistId, ex2.getIdentifier());
    }

    @Test
    void reportReplicDelegatesAndReturns() throws DomainException {
        UUID replicId = UUID.randomUUID();

        Replic replic = ReplicImpl.builder()
                .id(replicId).build();

        when(replicService.getReplicById(replicId)).thenReturn(Optional.of(replic));
        when(reportRepo.saveModel(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        Report returned = subject.reportReplic(replicId, null, "description");

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepo, times((1))).saveModel(reportCaptor.capture());

        assertEquals(returned, reportCaptor.getValue());
        assertNull(returned.getAuthorId());
        assertEquals("description", returned.getDescription());
    }

}
