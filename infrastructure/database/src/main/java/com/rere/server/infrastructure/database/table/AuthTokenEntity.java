package com.rere.server.infrastructure.database.table;

import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.domain.model.account.AuthTokenType;
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

import java.time.Instant;
import java.util.UUID;

/**
 * Entity class for the 'auth_tokens' table.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "auth_tokens")
public class AuthTokenEntity extends BaseEntity implements AuthToken {

    @Column(
            name = "expiration",
            nullable = false
    )
    private Instant expirationTimestamp;

    @Column(
            name = "token",
            nullable = false,
            unique = true
    )
    private UUID token;

    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private AccountEntity account;
    @Column(
            name = "invalidated",
            nullable = false
    )
    private boolean invalidated;
    @Column(
            name = "type",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private AuthTokenType type;
    @Column(name = "data")
    private String data;

    public UUID getAccountId() {
        return account.getId();
    }

}
