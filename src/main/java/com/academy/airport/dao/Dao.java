package com.academy.airport.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {
    T save(T entity);

    void update(T entity);

    boolean delete(K id);

    Optional<T> findById(K id);

    List<T> findAll();
}
