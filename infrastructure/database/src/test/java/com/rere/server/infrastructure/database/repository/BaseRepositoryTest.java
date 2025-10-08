package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.model.impl.ReplicImpl;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.repository.AuthTokenRepository;
import com.rere.server.domain.repository.BaseRepository;
import com.rere.server.domain.repository.ReplicAccessRepository;
import com.rere.server.domain.repository.ReplicRepository;
import com.rere.server.domain.repository.ReportRepository;
import com.rere.server.domain.repository.ServerConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "logging.level.org.hibernate.SQL=TRACE",
        "logging.level.org.hibernate.type=TRACE",
})
public abstract class BaseRepositoryTest<M> {

    /**
     * The account repository.
     */
    @Autowired
    protected AccountRepository accountRepo;

    /**
     * The replic repository.
     */
    @Autowired
    protected ReplicRepository replicRepo;

    /**
     * The replic-access-repository.
     */
    @Autowired
    protected ReplicAccessRepository replicAccessRepo;

    /**
     * The auth-token repository.
     */
    @Autowired
    protected AuthTokenRepository authTokenRepo;

    /**
     * The server-config repository.
     */
    @Autowired
    protected ServerConfigRepository serverConfigRepo;

    /**
     * The report-repository.
     */
    @Autowired
    protected ReportRepository reportRepo;

    /**
     * The subject to test in the test.
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected BaseRepository<M> subject;

    /**
     * Creates an entity for a given id.
     * @param id The id of the entity.
     * @return The entity.
     */
    protected abstract M create(UUID id);

    /**
     * Creates a unique account for an id.
     * @param id The id.
     * @return The account.
     */
    protected Account createUniqueAccount(UUID id) {
        return AccountImpl.builder()
                .id(id)
                .email("%s@gmail.com".formatted(id.toString()))
                .username("user-%s".formatted(id.toString()))
                .build();
    }

    /**
     * Creates a unique account for an id.
     * @param id The id.
     * @return The account.
     */
    protected Replic createUniqueReplic(UUID id) {
        return ReplicImpl.builder()
                .id(id)
                .build();
    }

    /**
     * Inserts the dependencies of a specific model into the database, i.e. referenced records.
     * @param model The model.
     */
    protected abstract void insertDependencies(M model);

    @AfterEach
    public void setUp() {
        log.info("About to delete all entries in all tables.");
        Stream.of(replicAccessRepo, reportRepo, replicRepo, authTokenRepo, accountRepo, serverConfigRepo)
                .map(BaseRepository.class::cast)
                .forEach(BaseRepository::clear);
    }

    @Test
    void saveWorks() {
        for (int i = 0; i < 12; i++) {
            M model = create(UUID.randomUUID());
            insertDependencies(model);
            subject.saveModel(model);
        }

        List<M> returned = subject.getAll();
        assertEquals(12, returned.size());
    }

    @Test
    void deleteWorks() {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            ids.add(UUID.randomUUID());
        }
        for (int i = 0; i < 12; i++) {
            M model = create(ids.get(i));
            log.info("Will save dependencies for model {} inside deleteWorks().", model);
            insertDependencies(model);
            subject.saveModel(model);
        }

        Optional<M> returned1 = subject.delete(ids.get(4));
        Optional<M> returned2 = subject.delete(ids.get(9));

        List<M> returned = subject.getAll();

        assertEquals(10, returned.size());
        assertTrue(returned1.isPresent());
        assertTrue(returned2.isPresent());
    }

    @Test
    void deleteReturnsEmptyForInvalidId() {
        for (int i = 0; i < 12; i++) {
            M model = create(UUID.randomUUID());
            insertDependencies(model);
            subject.saveModel(model);
        }

        Optional<M> returned1 = subject.delete(UUID.randomUUID());

        List<M> returned = subject.getAll();

        assertEquals(12, returned.size());
        assertFalse(returned1.isPresent());
    }

}
