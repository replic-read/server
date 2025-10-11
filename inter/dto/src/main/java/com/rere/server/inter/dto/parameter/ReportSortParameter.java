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
            case USER -> Comparator.comparing(Report::getAuthorId);
        };

        return SortDirectionParameter.ASCENDING.equals(direction) ? comparator : comparator.reversed();
    }
}
