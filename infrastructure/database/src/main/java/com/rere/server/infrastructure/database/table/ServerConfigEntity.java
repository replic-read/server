package com.rere.server.infrastructure.database.table;

import com.rere.server.domain.model.config.AuthUserGroup;
import com.rere.server.domain.model.config.ReplicLimitConfig;
import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.model.impl.ReplicLimitConfigImpl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.time.Period;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "server_config")
public class ServerConfigEntity extends BaseEntity implements ServerConfig {

    @Column(
            name = "create_replic_group",
            nullable = false
    )
    private AuthUserGroup createReplicsGroup;

    @Column(
            name = "access_replic_group",
            nullable = false
    )
    private AuthUserGroup accessReplicsGroup;

    @Column(
            name = "create_report_group",
            nullable = false
    )
    private AuthUserGroup createReportsGroup;

    @Column(
            name = "allow_signup",
            nullable = false
    )
    private boolean allowAccountCreation;

    @Column(
            name = "limit_period"
    )
    private Period limitPeriod;

    @Column(
            name = "limit_period_start"
    )
    private Instant limitPeriodStart;

    @Column(
            name = "limit_count"
    )
    private Integer limitCount;

    @Column(
            name = "min_exp_period"
    )
    private Period maximumActivePeriod;

    @Override
    public ReplicLimitConfig getLimit() {
        if (getLimitCount() != null &&
            getLimitPeriod() != null &&
            getLimitPeriodStart() != null) {
            return new ReplicLimitConfigImpl(
                    getLimitPeriod(),
                    getLimitPeriodStart(),
                    getLimitCount());
        } else {
            return null;
        }
    }

    @Override
    public void setLimit(@NonNull ReplicLimitConfig limit) {
        limitPeriod = limit.getPeriod();
        limitPeriodStart = limit.getPeriodStart();
        limitCount = limit.getCount();
    }

}
