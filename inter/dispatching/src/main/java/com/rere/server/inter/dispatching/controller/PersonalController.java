package com.rere.server.inter.dispatching.controller;

import com.rere.server.inter.dispatching.documentation.endpoint.EndpointMetadata;
import com.rere.server.inter.dto.request.UpdateAccountRequest;
import com.rere.server.inter.dto.response.AccountResponse;
import com.rere.server.inter.dto.response.QuotaProgressResponse;
import com.rere.server.inter.execution.PersonalExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.ACCOUNT_UNIQUE;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.BAD_AUTHENTICATION;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.SUCCESS;
import static com.rere.server.inter.dispatching.documentation.endpoint.AuthorizationType.LOGGED_IN;

/**
 * The web-controller for personal matters.
 */
@Tag(
        name = "Personal",
        description = "Handles the different actions related to the account of a user."
)
@RestController
@RequestMapping("/me")
public class PersonalController {

    private final PersonalExecutor executor;

    @Autowired
    public PersonalController(PersonalExecutor executor) {
        this.executor = executor;
    }

    @Operation(
            summary = "Get account info",
            description = "Gets information about the currently authenticated account."
    )
    @EndpointMetadata(
            authorizationType = LOGGED_IN,
            responseTypes = {SUCCESS, BAD_AUTHENTICATION}
    )
    @GetMapping("/")
    public AccountResponse getMe() {
        return executor.getMe();
    }

    @Operation(
            summary = "Update account info",
            description = "Updates data of the account."
    )
    @EndpointMetadata(
            authorizationType = LOGGED_IN,
            responseTypes = {SUCCESS,
                    BAD_AUTHENTICATION,
                    ACCOUNT_UNIQUE}
    )
    @PostMapping("/")
    public AccountResponse updateMe(@Valid @RequestBody UpdateAccountRequest request) {
        return executor.updateMe(request);
    }

    @Operation(
            summary = "Get replic quota progress",
            description = "Gets the count of replics created in the current limit period."
    )
    @EndpointMetadata(
            authorizationType = LOGGED_IN,
            responseTypes = {SUCCESS,
                    BAD_AUTHENTICATION}
    )
    @GetMapping("/quota/")
    public QuotaProgressResponse getQuotaProgress() {
        return executor.getQuotaProgress();
    }
}
