package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.impl.ReplicLimitConfigImpl;
import com.rere.server.domain.model.impl.ServerConfigImpl;
import com.rere.server.domain.repository.ServerConfigRepository;
import com.rere.server.infrastructure.database.repository.jpa.ServerConfigCrudRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "logging.level.org.hibernate.SQL=TRACE",
        "logging.level.org.hibernate.type=TRACE",
})
class ServerConfigRepositoryTest {

    @Autowired
    private ServerConfigCrudRepository crudRepo;

    @Autowired
    private ServerConfigRepository serverConfigRepo;

    @AfterEach
    void tearDown() {
        crudRepo.deleteAll();
    }

    @Test
    void getConfigReturnsEmptyIfNotSet() {
        Optional<ServerConfig> config = serverConfigRepo.getConfig();

        assertTrue(config.isEmpty());
    }

    @Test
    void getConfigReturnsSetValue() {
        Instant now = Instant.now()
                .truncatedTo(ChronoUnit.MILLIS);
        ServerConfig config = ServerConfigImpl.builder()
                .limit(ReplicLimitConfigImpl.builder()
                        .count(5)
                        .period(Period.ZERO)
                        .periodStart(now)
                        .build())
                .build();

        serverConfigRepo.saveConfig(config);

        Optional<ServerConfig> returned = serverConfigRepo.getConfig();

        assertTrue(returned.isPresent());
        assertEquals(5, returned.get().getLimit().getCount());
        assertEquals(Period.ZERO, returned.get().getLimit().getPeriod());
        assertEquals(now, returned.get().getLimit().getPeriodStart());
    }

}
