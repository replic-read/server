package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.AccountState;
import com.rere.server.infrastructure.database.repository.jpa.AccountCrudRepository;
import com.rere.server.infrastructure.database.table.AccountEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.UUID;

@DataJpaTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
class AccountCrudRepositoryTest {

    @Autowired
    private AccountCrudRepository subject;

    @Test
    void saveWorks() {
        AccountEntity entity = new AccountEntity("email", "username", "password", false, 0, AccountState.ACTIVE);
        entity.setId(UUID.randomUUID());
        entity.setCreationTimestamp(Instant.now());
        subject.save(entity);
    }

}
