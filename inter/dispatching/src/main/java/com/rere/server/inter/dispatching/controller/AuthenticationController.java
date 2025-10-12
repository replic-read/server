package com.rere.server.inter.dispatching.controller;

import com.rere.server.inter.dispatching.documentation.endpoint.EndpointMetadata;
import com.rere.server.inter.dto.request.CreateAccountRequest;
import com.rere.server.inter.dto.request.CredentialsRequest;
import com.rere.server.inter.dto.request.RefreshRequest;
import com.rere.server.inter.dto.request.SubmitEmailVerificationRequest;
import com.rere.server.inter.dto.response.AccountWithTokensResponse;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;
import com.rere.server.inter.execution.AuthenticationExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.ACCOUNT_UNIQUE;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.BAD_AUTHENTICATION;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.LOGIN_MISSING_IDENTIFICATION;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.REQUEST_EMAIL_TOKEN_ALREADY_VERIFIED;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.REQUEST_EMAIL_TOKEN_SUCCESS;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.SIGNUP_ACCOUNT_CREATED;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.SIGNUP_ACCOUNT_CREATED_EMAIL_SENT;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.SIGNUP_DISABLED;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.SUBMIT_EMAIL_TOKEN_BAD_TOKEN;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.SUCCESS;
import static com.rere.server.inter.dispatching.documentation.endpoint.AuthorizationType.LOGGED_IN;
import static com.rere.server.inter.dto.validation.FieldType.HTML_EMAIL;

/**
 * The web-controller for authentication matters.
 */
@Tag(
        name = "Authentication",
        description = "Endpoints that allow clients to authenticate, create accounts and verify their email address."
)
@RestController
@RequestMapping(path = "/auth")
public class AuthenticationController {

    private final AuthenticationExecutor executor;

    @Autowired
    public AuthenticationController(AuthenticationExecutor executor) {
        this.executor = executor;
    }

    @Operation(
            summary = "Submit email-verification token",
            description = "Submits an email-verification token that was sent to a user."
    )
    @EndpointMetadata(
            responseTypes = {SUCCESS, SUBMIT_EMAIL_TOKEN_BAD_TOKEN}
    )
    @PostMapping("/submit-email-verification/")
    public void submitEmailVerification(@Valid @RequestBody SubmitEmailVerificationRequest request) {
        executor.submitEmailVerification(request);
    }

    @Operation(
            summary = "Create new account",
            description = "Creates a new account. After the account has been added, the credentials can be used to authenticate. " +
                          "If required, an email-verification-email will immediately be sent to the user's email address."
    )
    @EndpointMetadata(
            responseTypes = {SIGNUP_ACCOUNT_CREATED,
                    SIGNUP_ACCOUNT_CREATED_EMAIL_SENT,
                    SIGNUP_DISABLED,
                    ACCOUNT_UNIQUE}
    )
    @PostMapping("/signup/")
    public AccountWithTokensResponse signup(
            @Valid @RequestBody CreateAccountRequest request,
            @ValidationMetadata(value = FieldType.SEND_VERIFICATION_EMAIL, required = false) @Valid @RequestParam(name = "send_email", defaultValue = "true") boolean sendEmail) {
        return executor.signup(request, sendEmail);
    }

    @Operation(
            summary = "Refresh",
            description = "Creates a new access-token and refresh-token by providing an existing refresh-token."
    )
    @EndpointMetadata(
            responseTypes = {SUCCESS, BAD_AUTHENTICATION}
    )
    @PostMapping("/refresh/")
    public AccountWithTokensResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return executor.refresh(request);
    }

    @Operation(
            summary = "Log in account",
            description = "Logs into an existing account with either username or email and password."
    )
    @EndpointMetadata(
            responseTypes = {SUCCESS,
                    BAD_AUTHENTICATION,
                    LOGIN_MISSING_IDENTIFICATION}
    )
    @PostMapping("/login/")
    public AccountWithTokensResponse login(@Valid @RequestBody CredentialsRequest request) {
        return executor.login(request);
    }

    @Operation(
            summary = "Request email-verification token",
            description = "Requests to send an email containing a link to verify the email-address of the user."
    )
    @EndpointMetadata(
            authorizationType = LOGGED_IN,
            responseTypes = {REQUEST_EMAIL_TOKEN_SUCCESS,
                    REQUEST_EMAIL_TOKEN_ALREADY_VERIFIED,
                    BAD_AUTHENTICATION}
    )
    @GetMapping("/request-email-verification/")
    public void requestEmailVerification(@ValidationMetadata(value = HTML_EMAIL, required = false) @Valid @RequestParam(defaultValue = "true") boolean html) {
        executor.requestEmailVerification(html);
    }
}
