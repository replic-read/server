package com.rere.server.inter.execution.impl;

import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.AuthorizationException;
import com.rere.server.inter.authorization.Authorizer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public abstract class BaseExecutorTest {

    @Mock
    protected AccountService accountService;
    @Mock
    protected AuthenticationService authService;
    @Mock
    protected ReplicService replicService;
    @Mock
    protected ReportService reportService;
    @Mock
    protected ServerConfigService configService;
    @Mock
    protected QuotaService quotaService;
    @Mock(strictness = Mock.Strictness.LENIENT)
    protected Authorizer authorizer;

    protected void assertAuthorizationIsPropagated(Runnable call) {
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireAccessReplics(any());
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireCreateReplics(any());
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireCreateAccount(any());
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireAccessAccountsFull(any());
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireChangeServerConfig(any());
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireReviewReports(any());
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireAccessReports(any());
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireCreateReports(any());
        doThrow(AuthorizationException.genericUnauthorized()).when(authorizer).requireUpdateReplicState(any(), any(), any());

        assertThrows(AuthorizationException.class, call::run);
    }

}
