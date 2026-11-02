package com.ecommerce.repository;

import com.ecommerce.model.Categorie;

public class CategorieRepository extends InMemoryRepository<Categorie, Long> {
    public CategorieRepository() {
        super(Categorie::getId);
    }
}
