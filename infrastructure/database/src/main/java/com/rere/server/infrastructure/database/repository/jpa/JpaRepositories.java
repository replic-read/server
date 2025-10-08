package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.AccountEntity;
import com.rere.server.infrastructure.database.table.AuthTokenEntity;
import com.rere.server.infrastructure.database.table.ReplicAccessEntity;
import com.rere.server.infrastructure.database.table.ReplicEntity;
import com.rere.server.infrastructure.database.table.ReportEntity;
import com.rere.server.infrastructure.database.table.ServerConfigEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Contains the jpa crud repositories.
 */
public final class JpaRepositories {

    /**
     * The account crud interface.
     */
    @Repository
    public interface Account extends CrudRepository<AccountEntity, UUID> {
    }

    /**
     * The auth-token crud interface.
     */
    @Repository
    public interface AuthToken extends CrudRepository<AuthTokenEntity, UUID> {
    }

    /**
     * The replic-access crud interface.
     */
    @Repository
    public interface ReplicAccess extends CrudRepository<ReplicAccessEntity, UUID> {
    }

    /**
     * The replic crud interface.
     */
    @Repository
    public interface Replic extends CrudRepository<ReplicEntity, UUID> {
    }

    /**
     * The report crud interface.
     */
    @Repository
    public interface Report extends CrudRepository<ReportEntity, UUID> {
    }

    /**
     * The server-config crud interface.
     */
    @Repository
    public interface ServerConfig extends CrudRepository<ServerConfigEntity, UUID> {
    }

}
