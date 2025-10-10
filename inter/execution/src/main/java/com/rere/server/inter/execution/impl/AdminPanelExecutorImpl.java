package com.rere.server.inter.execution.impl;

import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.Authorizer;
import com.rere.server.inter.execution.AbstractExecutor;
import com.rere.server.inter.execution.AdminPanelExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AdminPanelExecutorImpl extends AbstractExecutor implements AdminPanelExecutor {

    private final ApplicationContext context;

    @Autowired
    protected AdminPanelExecutorImpl(AccountService accountService, AuthenticationService authService, ReplicService replicService, ReportService reportService, ServerConfigService configService, QuotaService quotaService, Authorizer authorizer, ApplicationContext context) {
        super(accountService, authService, replicService, reportService, configService, quotaService, authorizer);
        this.context = context;
    }

    private void schedule(Runnable runnable) {
        Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(300);
                runnable.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void shutdown() {
        authorizer.requireChangeServerConfig(getAuth());

        schedule(() -> SpringApplication.exit(context));
    }
}
