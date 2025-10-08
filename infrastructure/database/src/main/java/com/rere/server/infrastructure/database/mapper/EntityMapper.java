package com.rere.server.infrastructure.database.mapper;

/**
 * A class that maps an entity to a model.
 *
 * @param <M> The model to map to.
 * @param <E> The entity to map from.
 */
public interface EntityMapper<E, M> {

    /**
     * Maps a model into an entity.
     * @param model The model to map.
     * @return The mapped entity.
     */
    E map(M model);

}
