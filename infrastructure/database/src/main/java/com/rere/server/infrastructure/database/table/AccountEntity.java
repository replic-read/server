package com.rere.server.infrastructure.database.table;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class for the 'users' table.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
public class AccountEntity extends BaseEntity implements Account {

    @Column(
            name = "email",
            unique = true,
            nullable = false
    )
    private String email;

    @Column(
            name = "username",
            unique = true,
            nullable = false
    )
    private String username;

    @Column(
            name = "password_hash",
            nullable = false
    )
    private String passwordHash;

    @Column(
            name = "is_admin",
            nullable = false
    )
    private boolean isAdmin;

    @Column(
            name = "profile_color",
            nullable = false
    )
    private int profileColor;

    @Column(
            name = "state",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private AccountState accountState;

}
