package com.rere.server.inter.execution;

/**
 * Executor for admin-related endpoints.
 */
public interface AdminPanelExecutor {

    /**
     * Executor for POST /admin/shutdown/.
     */
    void shutdown();

}
