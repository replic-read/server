package com.rere.server.inter.dto.parameter;

import com.rere.server.domain.model.replic.Replic;

import java.util.Comparator;

public enum ReplicSortParameter implements SortParameter<Replic> {

    DATE,

    EXPIRATION,

    SIZE,

    ORIGIN;

    @Override
    public Comparator<Replic> getComparator(SortDirectionParameter direction) {
        Comparator<Replic> comparator = switch (this) {
            case DATE -> Comparator.comparing(Replic::getCreationTimestamp);
            case SIZE -> Comparator.comparing(Replic::getSize);
            case ORIGIN -> Comparator.comparing(r -> r.getOriginalUrl().toString());
            case EXPIRATION -> Comparator.comparing(Replic::getExpirationTimestamp);
        };

        return SortDirectionParameter.ASCENDING.equals(direction) ? comparator : comparator.reversed();
    }
}
