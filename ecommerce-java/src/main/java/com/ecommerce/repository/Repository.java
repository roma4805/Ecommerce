package com.ecommerce.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<T, ID> {
    T save(T entite);
    Optional<T> findById(ID id);
    List<T> findAll();
    List<T> findBy(Predicate<T> filtre);
    void deleteById(ID id);
    long count();
}
