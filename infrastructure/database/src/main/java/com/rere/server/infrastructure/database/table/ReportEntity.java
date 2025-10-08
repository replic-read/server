package com.rere.server.infrastructure.database.table;

import com.rere.server.domain.model.report.Report;
import com.rere.server.domain.model.report.ReportState;
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
import lombok.NonNull;

import java.util.UUID;

/**
 * Entity class for the 'reports' table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reports")
public class ReportEntity extends BaseEntity implements Report {

    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "replic_id",
            nullable = false
    )
    private ReplicEntity replic;
    @ManyToOne
    @JoinColumn(
            name = "author_id"
    )
    private AccountEntity author;
    @Column(
            name = "description"
    )
    private String description;
    @Column(
            name = "state",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private ReportState state;

    @Override
    public @NonNull UUID getReplicId() {
        return replic.getId();
    }

    @Override
    public UUID getAuthorId() {
        return author.getId();
    }

}
