package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.service.BaseDomainServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link AccountServiceImpl} class.
 */
class AccountServiceImplTest extends BaseDomainServiceTest {

    @Mock
    private AccountRepository accountRepo;

    @InjectMocks
    private AccountServiceImpl subject;

    private static Account createAccount(UUID id) {
        return new AccountImpl(id, Instant.now(), "", "", "", false, AccountState.ACTIVE, 0);
    }

    private static Account createAccount(AccountState state) {
        return new AccountImpl(UUID.randomUUID(), Instant.now(), "", "", "", false, state, 0);
    }

    @Test
    void getAccountsSortWorks() {
        List<UUID> idList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            idList.add(UUID.randomUUID());
        }

        List<UUID> shuffledList = new ArrayList<>(idList);
        Collections.shuffle(shuffledList);

        // Accounts are in same order as shuffled list.
        List<Account> accounts = new ArrayList<>();
        for (UUID id : shuffledList) {
            accounts.add(createAccount(id));
        }

        // Comparator where the order is the order of the id's in the original list.
        Comparator<Account> comparator = Comparator.comparingInt(c -> idList.indexOf(c.getId()));

        when(accountRepo.getAll()).thenReturn(accounts);

        List<Account> sortedAccounts = subject.getAccounts(comparator, null, null, null);

        // Check that the id of each index matches the id at the same index in the original id list.
        for (int i = 0; i < sortedAccounts.size(); i++) {
            Assertions.assertEquals(idList.get(i), sortedAccounts.get(i).getId());
        }
    }

    @Test
    void getAccountsFiltersId() {
        UUID specialId = UUID.randomUUID();
        List<Account> accounts = new ArrayList<>();
        accounts.add(createAccount(specialId));
        for (int i = 1; i < 10; i++) {
            accounts.add(createAccount(UUID.randomUUID()));
        }

        when(accountRepo.getAll()).thenReturn(accounts);

        List<Account> filteredAccounts = subject.getAccounts(null, specialId, null, null);

        Assertions.assertEquals(1, filteredAccounts.size());
        Assertions.assertEquals(accounts.getFirst(), filteredAccounts.getFirst());
    }

    @Test
    void getAccountsFiltersState() {
        Set<AccountState> filter1 = Set.of(AccountState.ACTIVE, AccountState.INACTIVE);
        Set<AccountState> filter2 = Set.of();

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            accounts.add(createAccount(AccountState.ACTIVE));
            accounts.add(createAccount(AccountState.INACTIVE));
            accounts.add(createAccount(AccountState.UNVERIFIED));
        }
        Collections.shuffle(accounts);

        when(accountRepo.getAll()).thenReturn(accounts);

        List<Account> filtered1 = subject.getAccounts(null, null, filter1, null);
        List<Account> filtered2 = subject.getAccounts(null, null, filter2, null);

        for (Account account : filtered1) {
            assertTrue(filter1.contains(account.getAccountState()));
        }
        Assertions.assertEquals(0, filtered2.size());
    }

    @Test
    void getAccountsFiltersQuery() {
        String query = "life, the universe and everything";
        Account account1 = new AccountImpl(UUID.randomUUID(), Instant.now(), "sdf4r4" + query + "andsciuasi", "", "", false, AccountState.ACTIVE, 0);
        Account account2 = new AccountImpl(UUID.randomUUID(), Instant.now(), "", "aeqe2" + query + "adasda", "", false, AccountState.ACTIVE, 0);
        List<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        for (int i = 0; i < 10; i++) {
            accounts.add(createAccount(UUID.randomUUID()));
        }
        Collections.shuffle(accounts);

        when(accountRepo.getAll()).thenReturn(accounts);

        List<Account> queriedAccounts = subject.getAccounts(null, null, null, query);
        Assertions.assertEquals(2, queriedAccounts.size());
        assertTrue(queriedAccounts.contains(account1));
        assertTrue(queriedAccounts.contains(account2));
    }

    @Test
    void getByEmailWorks() {
        List<Account> accounts = IntStream
                .range(0, 10)
                .mapToObj(i -> AccountImpl.builder().email("user%d@gmail.com".formatted(i)).build())
                .map(Account.class::cast)
                .toList();

        when(accountRepo.getAll()).thenReturn(accounts);

        assertTrue(subject.getByEmail("user5@gmail.com").isPresent());
        assertTrue(subject.getByEmail("user55@gmail.com").isEmpty());
    }

    @Test
    void updateAccountThrowsForUnknownAccount() {
        when(accountRepo.getAll()).thenReturn(List.of());

        assertThrows(NotFoundException.class,
                () -> subject.updateAccount(UUID.randomUUID(), null, null, 0));
    }

    @Test
    void updateAccountThrowsForNotUnique() {
        UUID id = UUID.randomUUID();
        List<Account> accounts = List.of(
                AccountImpl.builder().id(id).build(),
                AccountImpl.builder().email("email1").username("username1").build(),
                AccountImpl.builder().email("email2").username("username2").build()
        );
        when(accountRepo.getAll()).thenReturn(accounts);

        assertThrows(NotUniqueException.class,
                () -> subject.updateAccount(id, "email1", null, 0));

        assertThrows(NotUniqueException.class,
                () -> subject.updateAccount(id, "email3", "username2", 0));
    }

    @Test
    void updateAccountUpdatesPropertiesAndState() throws DomainException {
        UUID id = UUID.randomUUID();

        when(accountRepo.getAll()).thenReturn(List.of(AccountImpl.builder()
                .id(id)
                .accountState(AccountState.ACTIVE).build()));
        when(accountRepo.saveModel(any())).thenAnswer(inv -> inv.getArguments()[0]);

        subject.updateAccount(id, "email1", "username1", 3);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.captor();

        verify(accountRepo, times(1)).saveModel(accountCaptor.capture());

        assertEquals(id, accountCaptor.getValue().getId());
        assertEquals(3, accountCaptor.getValue().getProfileColor());
        assertEquals("email1", accountCaptor.getValue().getEmail());
        assertEquals("username1", accountCaptor.getValue().getUsername());
        assertEquals(AccountState.UNVERIFIED, accountCaptor.getValue().getAccountState());
    }

    @Test
    void getByUsernameWorks() {
        List<Account> accounts = IntStream
                .range(0, 10)
                .mapToObj(i -> AccountImpl.builder().username("user%d".formatted(i)).build())
                .map(Account.class::cast)
                .toList();

        when(accountRepo.getAll()).thenReturn(accounts);

        assertTrue(subject.getByUsername("user5").isPresent());
        assertTrue(subject.getByUsername("user55").isEmpty());
    }

}
