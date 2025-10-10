package com.rere.server.inter.execution.dto.parameter;

import java.util.Comparator;

/**
 * A sort parameter.
 * @param <M> The model to compare.
 */
public interface SortParameter<M> {

    /**
     * Provides the comparator for the specific sort parameter.
     * @param direction The direction.
     * @return The comparator.
     */
    Comparator<M> getComparator(SortDirectionParameter direction);

}
