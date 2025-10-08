package com.rere.server.infrastructure.database.table;

import com.rere.server.domain.model.replic.ReplicAccess;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

/**
 * Entity class for the 'replic_accesses' table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "replic_accesses")
public class ReplicAccessEntity extends BaseEntity implements ReplicAccess {

    @ManyToOne
    @JoinColumn(
            name = "visitor_id"
    )
    private AccountEntity visitor;
    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "replic_id",
            nullable = false
    )
    private ReplicEntity replic;

    @Override
    public UUID getVisitorId() {
        return visitor.getId();
    }

    @Override
    public @NonNull UUID getReplicId() {
        return replic.getId();
    }

}
