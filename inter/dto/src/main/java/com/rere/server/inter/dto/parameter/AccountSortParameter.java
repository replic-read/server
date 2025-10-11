package com.rere.server.inter.dto.parameter;

import com.rere.server.domain.model.account.Account;

import java.util.Comparator;

public enum AccountSortParameter implements SortParameter<Account> {

    USERNAME,

    STATUS,

    CREATION;

    @Override
    public Comparator<Account> getComparator(SortDirectionParameter direction) {
        Comparator<Account> comparator = switch (this) {
            case USERNAME -> Comparator.comparing(Account::getUsername);
            case STATUS -> Comparator.comparing(Account::getAccountState);
            case CREATION -> Comparator.comparing(Account::getCreationTimestamp);
        };

        return SortDirectionParameter.ASCENDING.equals(direction) ? comparator : comparator.reversed();
    }
}
