package com.rere.server.infrastructure.database.table;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

/**
 * Base entity that contains columns that every entity in our database has.
 */
@Data
@EqualsAndHashCode
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(
            name = "id",
            unique = true,
            nullable = false
    )
    private UUID id;

    @Column(
            name = "created_timestamp",
            nullable = false
    )
    private Instant creationTimestamp;
}
