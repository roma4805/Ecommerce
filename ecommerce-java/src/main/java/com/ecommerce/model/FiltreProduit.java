package com.ecommerce.model;

import java.math.BigDecimal;

@FunctionalInterface
public interface FiltreProduit {
        boolean accepter(Produit p);

    // ---- Methodes default : combiner des filtres ----

    default FiltreProduit et(FiltreProduit autre) {
        return p -> this.accepter(p) && autre.accepter(p);
    }

    default FiltreProduit ou(FiltreProduit autre) {
        return p -> this.accepter(p) || autre.accepter(p);
    }

    default FiltreProduit inverse() {
        return p -> !this.accepter(p);
    }

    // ---- Methodes static : fabriques de filtres prets a l'emploi ----

    static FiltreProduit disponibles() {
        return p -> p.isActif() && p.getStock() > 0;
    }

    static FiltreProduit parCategorie(Categorie categorie) {
        return p -> categorie.equals(p.getCategorie());
    }

    static FiltreProduit prixMax(BigDecimal max) {
        return p -> p.getPrix().compareTo(max) <= 0;
    }

    static FiltreProduit noteMin(double note) {
        return p -> p.getNoteMoyenne() >= note;
    }
    
}
