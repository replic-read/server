package com.rere.server.infrastructure.database.table;

import com.rere.server.domain.model.replic.MediaMode;
import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.domain.model.replic.ReplicState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity class for the 'replics' table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "replics")
public class ReplicEntity extends BaseEntity implements ReplicBaseData {

    @Column(
            name = "original_link",
            nullable = false
    )
    private String originalUrl;
    @Column(
            name = "media_mode",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private MediaMode mediaMode;
    @Column(
            name = "state",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private ReplicState state;
    @Column(
            name = "description"
    )
    @Enumerated(EnumType.STRING)
    private String description;
    @Column(
            name = "expiration",
            nullable = false
    )
    private Instant expirationTimestamp;
    @Column(
            name = "password_hash"
    )
    private String passwordHash;
    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "author_id",
            nullable = false
    )
    private AccountEntity owner;

    public URL getOriginalUrl() {
        try {
            return URI.create(originalUrl).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public UUID getOwnerId() {
        return owner.getId();
    }

}
