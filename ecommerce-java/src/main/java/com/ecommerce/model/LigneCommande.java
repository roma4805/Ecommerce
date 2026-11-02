package com.ecommerce.model;

import java.math.BigDecimal;
import java.util.Objects;

public class LigneCommande {
    private int quantite;
    private BigDecimal prixUnitaire;
    private String nomProduit;
    private final Produit produit;

    public LigneCommande(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrix();
        this.nomProduit = produit.getNom();
    }

    public BigDecimal calculerSousTotal() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    public int getQuantite() { return quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public String getNomProduit() { return nomProduit; }
    public Produit getProduit() { return produit; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LigneCommande)) return false;
        LigneCommande l = (LigneCommande) o;
        return quantite == l.quantite && Objects.equals(prixUnitaire, l.prixUnitaire)
                && Objects.equals(nomProduit, l.nomProduit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantite, prixUnitaire, nomProduit);
    }

    @Override
    public String toString() {
        return "LigneCommande{nomProduit='" + nomProduit + "', quantite=" + quantite
                + ", prixUnitaire=" + prixUnitaire + "}";
    }
}
