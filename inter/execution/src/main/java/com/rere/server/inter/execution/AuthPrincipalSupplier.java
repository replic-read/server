package com.rere.server.inter.execution;

import com.rere.server.domain.model.account.Account;

import java.util.function.Supplier;

public interface AuthPrincipalSupplier extends Supplier<Account> {
}
