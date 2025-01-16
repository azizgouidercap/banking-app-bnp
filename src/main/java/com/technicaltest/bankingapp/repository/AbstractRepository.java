package com.technicaltest.bankingapp.repository;

import com.technicaltest.bankingapp.database.InMemoryDatabase;
import com.technicaltest.bankingapp.model.Entity;

import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractRepository<T extends Entity> {

    protected final Map<Long, T> collection;
    private final AtomicLong idCounter;

    @SuppressWarnings("unchecked")
    public AbstractRepository() {
        // Infer the actual entity class using reflection
        Class<T> entityType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.collection = InMemoryDatabase.getCollection(entityType);
        this.idCounter = InMemoryDatabase.getIdCounter(entityType);
    }

    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idCounter.incrementAndGet());
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());
        collection.put(entity.getId(), entity);
        return entity;
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(collection.get(id));
    }
}
