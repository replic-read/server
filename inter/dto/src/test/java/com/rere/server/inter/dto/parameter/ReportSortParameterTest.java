package com.rere.server.inter.dto.parameter;

import com.rere.server.domain.model.impl.ReportImpl;
import com.rere.server.domain.model.report.Report;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportSortParameterTest {

    private static List<Report> reports = IntStream.range(0, 20)
            .mapToObj(i -> (Report) ReportImpl.builder()
                    .creationTimestamp(Instant.now().minusSeconds(i * 1000L))
                    .build())
            .toList();

    static {
        List<Report> shuffled = new ArrayList<>(reports);
        Collections.shuffle(shuffled);
        reports = shuffled;
    }

    <U extends Comparable<? super U>> void testWorksWith(Comparator<Report> field, ReportSortParameter parameter) {
        List<Report> expectedAsc = reports.stream()
                .sorted(field)
                .toList();
        List<Report> actualAsc = reports.stream()
                .sorted(parameter.getComparator(SortDirectionParameter.ASCENDING))
                .toList();
        List<Report> expectedDesc = reports.stream()
                .sorted(field.reversed())
                .toList();
        List<Report> actualDesc = reports.stream()
                .sorted(parameter.getComparator(SortDirectionParameter.DESCENDING))
                .toList();

        assertEquals(expectedAsc, actualAsc);
        assertEquals(expectedDesc, actualDesc);
    }

    @Test
    void getComparatorWorksDate() {
        testWorksWith(Comparator.comparing(Report::getCreationTimestamp), ReportSortParameter.DATE);
    }

    @Test
    void getComparatorWorksCreation() {
        testWorksWith(new ReportUserComparator(), ReportSortParameter.USER);
    }

    private static class ReportUserComparator implements Comparator<Report> {
        @Override
        public int compare(Report r1, Report r2) {
            if (r2.getAuthorId() == null && r1.getAuthorId() == null) {
                return 0;
            } else if (r2.getAuthorId() == null) {
                return 1;
            } else {
                return r1.getAuthorId().compareTo(r2.getAuthorId());
            }
        }
    }
}