package com.ecommerce.repository;

import com.ecommerce.model.Avis;

public class AvisRepository extends InMemoryRepository<Avis, Long> {
    public AvisRepository() {
        super(Avis::getId);
    }
}
