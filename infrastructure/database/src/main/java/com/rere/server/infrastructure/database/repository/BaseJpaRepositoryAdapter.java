package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.repository.BaseRepository;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

/**
 * Base class for any adapter that delegates calls to a {@link CrudRepository}
 */
public abstract class BaseJpaRepositoryAdapter<M, E extends M>
        implements BaseRepository<M>, CrudRepository<E, UUID> {

    /**
     * The mapper for the
     */
    protected final EntityMapper<E, M> mapper;

    protected BaseJpaRepositoryAdapter(EntityMapper<E, M> mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<M> getAll() {
        return StreamSupport.stream(findAll().spliterator(), false).map(e -> (M) e).toList();
    }

    @Override
    public Optional<M> getById(UUID id) {
        return findById(id).map(e -> e);
    }

    @Override
    public M saveModel(M m) {
        return save(mapper.map(m));
    }

    @Override
    public Optional<M> delete(UUID id) {
        Optional<M> m = getById(id);
        if (m.isPresent()) {
            deleteById(id);
        }
        return m;
    }
}
