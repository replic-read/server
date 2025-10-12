package com.rere.server.inter.dto.parameter;

import com.rere.server.domain.model.report.Report;

import java.util.Comparator;

public enum ReportSortParameter implements SortParameter<Report> {

    DATE,

    USER;

    @Override
    public Comparator<Report> getComparator(SortDirectionParameter direction) {
        Comparator<Report> comparator = switch (this) {
            case DATE -> Comparator.comparing(Report::getCreationTimestamp);
            case USER -> new ReportUserComparator();
        };

        return SortDirectionParameter.ASCENDING.equals(direction) ? comparator : comparator.reversed();
    }

    // Compares based on the user id.
    // 'null' is treated as low.
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
