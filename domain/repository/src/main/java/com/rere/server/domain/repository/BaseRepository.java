package com.rere.server.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository that provides basic CRUD-methods for other repositories.
 *
 * @param <M> The model that this repository provides access to.
 */
public interface BaseRepository<M> {

    /**
     * Gets all saved models.
     * @return The list of all models.
     */
    List<M> getAll();

    /**
     * Gets a model by its specific id.
     * @param id The id of the model.
     * @return The model, or an empty {@link Optional}.
     */
    Optional<M> getById(UUID id);

    /**
     * Saves a given model.
     * @param m The model to save.
     * @return The model, because it might have been changed in the process of saving.
     */
    M save(M m);

    /**
     * Deletes a model.
     * @param id The id of the model to delete.
     * @return The model, if it has been deleted, or an empty optional.
     */
    Optional<M> delete(UUID id);

}
