package com.rere.server.domain.model.report;

/**
 * States a report can be in.
 */
public enum ReportState {

    /**
     * The report is still opened.
     */
    OPEN,

    /**
     * The report has been closed, i.e. ignored.
     */
    CLOSED,

    /**
     * The report has been reviewed.
     */
    REVIEWED

}
