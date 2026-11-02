package com.ecommerce.repository;

import java.util.List;
import com.ecommerce.model.Produit;

public class ProduitRepository extends InMemoryRepository<Produit, Long> {
    public ProduitRepository() {
        super(Produit::getId);
    }

    public List<Produit> findActifs() {
        return findBy(Produit::isActif);
    }
}
