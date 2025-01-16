package com.technicaltest.bankingapp.database;

import com.technicaltest.bankingapp.model.Entity;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class InMemoryDatabase {

    public static final Map<Class<?>, Map<Long, ?>> database = new HashMap<>();
    public static final Map<Class<?>, AtomicLong> idCounter = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Entity> Map<Long, T> getCollection(Class<T> entityType) {
        return (Map<Long, T>) database.computeIfAbsent(entityType, k -> new HashMap<>());
    }

    public static <T extends Entity> AtomicLong getIdCounter(Class<T> entityType) {
        return idCounter.computeIfAbsent(entityType, k -> new AtomicLong(0));
    }

}
