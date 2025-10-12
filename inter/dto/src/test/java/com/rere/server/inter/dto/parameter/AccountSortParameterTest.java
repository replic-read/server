package com.rere.server.inter.dto.parameter;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.impl.AccountImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountSortParameterTest {

    private static List<Account> accounts = IntStream.range(0, 20)
            .mapToObj(i -> (Account) AccountImpl.builder()
                    .username(UUID.randomUUID().toString())
                    .creationTimestamp(Instant.now().minusSeconds(i * 1000L))
                    .accountState(
                            i <= 4 ? AccountState.ACTIVE :
                                    i <= 7 ? AccountState.INACTIVE :
                                            AccountState.UNVERIFIED
                    )
                    .build())
            .toList();

    static {
        List<Account> shuffled = new ArrayList<>(accounts);
        Collections.shuffle(shuffled);
        accounts = shuffled;
    }

    <U extends Comparable<? super U>> void testWorksWith(Function<? super Account, ? extends U> field, AccountSortParameter parameter) {
        List<Account> expectedAsc = accounts.stream()
                .sorted(Comparator.comparing(field))
                .toList();
        List<Account> actualAsc = accounts.stream()
                .sorted(parameter.getComparator(SortDirectionParameter.ASCENDING))
                .toList();
        List<Account> expectedDesc = accounts.stream()
                .sorted(Comparator.comparing(field).reversed())
                .toList();
        List<Account> actualDesc = accounts.stream()
                .sorted(parameter.getComparator(SortDirectionParameter.DESCENDING))
                .toList();

        assertEquals(expectedAsc, actualAsc);
        assertEquals(expectedDesc, actualDesc);
    }

    @Test
    void getComparatorWorksUsername() {
        testWorksWith(Account::getUsername, AccountSortParameter.USERNAME);
    }

    @Test
    void getComparatorWorksStatus() {
        testWorksWith(Account::getAccountState, AccountSortParameter.STATUS);
    }

    @Test
    void getComparatorWorksCreation() {
        testWorksWith(Account::getCreationTimestamp, AccountSortParameter.CREATION);
    }

}