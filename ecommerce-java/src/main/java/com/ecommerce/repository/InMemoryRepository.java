package com.ecommerce.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class InMemoryRepository<T, ID> implements Repository<T, ID> {
    private final Map<ID, T> donnees = new LinkedHashMap<>();
    private final Function<T, ID> extracteurId;

    protected InMemoryRepository(Function<T, ID> extracteurId) {
        this.extracteurId = extracteurId;
    }

    @Override
    public T save(T entite) {
        donnees.put(extracteurId.apply(entite), entite);
        return entite;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(donnees.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(donnees.values());
    }

    @Override
    public List<T> findBy(Predicate<T> filtre) {
        return donnees.values().stream().filter(filtre).collect(Collectors.toList());
    }

    @Override
    public void deleteById(ID id) {
        donnees.remove(id);
    }

    @Override
    public long count() {
        return donnees.size();
    }
}
