package com.rere.server.inter.dispatching.controller;

import com.rere.server.domain.model.account.AccountState;
import com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType;
import com.rere.server.inter.dispatching.documentation.endpoint.EndpointMetadata;
import com.rere.server.inter.dto.parameter.AccountSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.request.CreateAccountRequest;
import com.rere.server.inter.dto.response.AccountResponse;
import com.rere.server.inter.dto.response.PartialAccountResponse;
import com.rere.server.inter.dto.validation.FieldType;
import com.rere.server.inter.dto.validation.ValidationMetadata;
import com.rere.server.inter.execution.AccountExecutor;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.ACCOUNT_UNIQUE;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.BAD_AUTHENTICATION;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.NO_PERMISSION_NO_EXIST;
import static com.rere.server.inter.dispatching.documentation.endpoint.ApiResponseType.SUCCESS;
import static com.rere.server.inter.dispatching.documentation.endpoint.AuthorizationType.ADMIN;
import static com.rere.server.inter.dto.mapper.EnumMapper.mapToEnum;
import static com.rere.server.inter.dto.validation.FieldType.ACCOUNT_FILTER_STATE;
import static com.rere.server.inter.dto.validation.FieldType.ACCOUNT_ID;
import static com.rere.server.inter.dto.validation.FieldType.ACCOUNT_SORT;
import static com.rere.server.inter.dto.validation.FieldType.FILTER_QUERY;
import static com.rere.server.inter.dto.validation.FieldType.SORT_DIRECTION;

/**
 * The web-controller for account matters.
 */
@Tag(
        name = "Accounts",
        description = "Handles the different actions related to accounts."
)
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountExecutor<AccountSortParameter, AccountState, SortDirectionParameter, UUID> executor;

    @Autowired
    public AccountController(AccountExecutor<AccountSortParameter, AccountState, SortDirectionParameter, UUID> executor) {
        this.executor = executor;
    }

    @Operation(
            summary = "Create an account",
            description = "Creates a new account."
    )
    @EndpointMetadata(
            authorizationType = ADMIN,
            responseTypes = {SUCCESS,
                    ACCOUNT_UNIQUE,
                    NO_PERMISSION_NO_EXIST,
                    BAD_AUTHENTICATION}
    )
    @PostMapping("/")
    public AccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @ValidationMetadata(value = FieldType.SEND_VERIFICATION_EMAIL, required = false) @Valid @RequestParam(name = "send_email", defaultValue = "false") boolean sendVerificationEmail,
            @ValidationMetadata(value = FieldType.IS_DIRECTLY_VERIFIED, required = false) @Valid @RequestParam(value = "verified", defaultValue = "false") boolean verified) {
        return executor.createAccount(request, sendVerificationEmail, verified);
    }

    @Operation(
            summary = "Get all accounts partial",
            description = "Gets all accounts with redacted information, e.g. email, state are not included."
    )
    @EndpointMetadata(responseTypes = ApiResponseType.SUCCESS)
    @GetMapping("/partial/")
    public List<PartialAccountResponse> getAccountsPartial(
            @ValidationMetadata(value = ACCOUNT_SORT, required = false) @Valid @RequestParam(name = "sort", required = false) String sortMode,
            @ValidationMetadata(value = SORT_DIRECTION, required = false) @Valid @RequestParam(name = "direction", required = false) String sortDirection,
            @ValidationMetadata(value = ACCOUNT_ID, required = false) @Valid @RequestParam(name = "account_id", required = false) String accountId,
            @ValidationMetadata(value = FILTER_QUERY, required = false) @Valid @RequestParam(name = "query", required = false) String query
    ) {
        return executor.getAccountsPartial(
                mapToEnum(sortMode, AccountSortParameter.class),
                mapToEnum(sortDirection, SortDirectionParameter.class),
                accountId != null ? UUID.fromString(accountId) : null,
                query
        );
    }

    @Operation(
            summary = "Get all accounts full",
            description = "Gets all accounts with full information, e.g. email, state, etc.."
    )
    @EndpointMetadata(
            authorizationType = ADMIN,
            responseTypes = {ApiResponseType.SUCCESS,
                    ApiResponseType.NO_PERMISSION_NO_EXIST,
                    ApiResponseType.BAD_AUTHENTICATION}
    )
    @GetMapping("/full/")
    public List<AccountResponse> getAccountsFull(
            @ValidationMetadata(value = ACCOUNT_SORT, required = false) @Valid @RequestParam(name = "sort", required = false) String sortMode,
            @ValidationMetadata(value = SORT_DIRECTION, required = false) @Valid @RequestParam(name = "direction", required = false) String sortDirection,
            @ValidationMetadata(value = ACCOUNT_ID, required = false) @Valid @RequestParam(name = "account_id", required = false) String accountId,
            @ValidationMetadata(value = ACCOUNT_FILTER_STATE, required = false) @Valid @RequestParam(name = "filter", required = false) Set<String> stateFilter,
            @ValidationMetadata(value = FILTER_QUERY, required = false) @Valid @RequestParam(name = "query", required = false) String query
    ) {
        return executor.getAccountsFull(
                mapToEnum(sortMode, AccountSortParameter.class),
                mapToEnum(sortDirection, SortDirectionParameter.class),
                accountId != null ? UUID.fromString(accountId) : null,
                stateFilter.stream().map(state -> mapToEnum(state, AccountState.class)).collect(Collectors.toSet()),
                query
        );
    }
}
