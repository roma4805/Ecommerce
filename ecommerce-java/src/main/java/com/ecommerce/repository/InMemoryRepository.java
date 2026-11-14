
package com.ecommerce.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class InMemoryRepository<T, ID> implements Repository<T, ID> {

    protected final Map<ID, T> donnees = new LinkedHashMap<>();

    private final Function<T, ID> extracteurId;

    protected InMemoryRepository(Function<T, ID> extracteurId) {
        this.extracteurId = extracteurId;
    }

    @Override
    public T save(T entite) {
        ID id = extracteurId.apply(entite);
        donnees.put(id, entite);
        return entite;
    }

    @Override
    public void saveAll(Collection<? extends T> entites) {
        entites.forEach(this::save);
    }

    @Override
    public void copierVers(Collection<? super T> destination) {
        destination.addAll(donnees.values());
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(donnees.get(id));
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(donnees.values());
    }

    @Override
    public List<T> findBy(Predicate<? super T> filtre) {
        return donnees.values()
                .stream()
                .filter(filtre)
                .toList();
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
