
package com.ecommerce.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<T, ID> {

    T save(T entite);

    void saveAll(Collection<? extends T> entites);

    void copierVers(Collection<? super T> destination);

    Optional<T> findById(ID id);

    List<T> findAll();

    List<T> findBy(Predicate<? super T> filtre);

    void deleteById(ID id);

    long count();

    default Page<T> findPage(int numero, int taille) {
        if (numero < 0 || taille <= 0) {
            throw new IllegalArgumentException("Numero ou taille invalide");
        }

        List<T> elements = findAll();

        List<T> resultat = elements.stream()
                .skip((long) numero * taille)
                .limit(taille)
                .toList();

        return new Page<>(resultat, numero, taille);
    }
}
