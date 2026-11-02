package com.ecommerce.model;

import java.math.BigDecimal;
import java.util.Objects;

public class LignePanier {
    private int quantite;
    private BigDecimal prixUnitaire;
    private final Produit produit;

    public LignePanier(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrix();
    }

    public BigDecimal calculerSousTotal() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    public void modifierQuantite(int qte) {
        if (qte <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive");
        }
        this.quantite = qte;
    }

    public int getQuantite() { return quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public Produit getProduit() { return produit; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LignePanier)) return false;
        LignePanier l = (LignePanier) o;
        return quantite == l.quantite && Objects.equals(produit, l.produit)
                && Objects.equals(prixUnitaire, l.prixUnitaire);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produit, quantite, prixUnitaire);
    }

    @Override
    public String toString() {
        return "LignePanier{produit=" + produit.getNom() + ", quantite=" + quantite
                + ", prixUnitaire=" + prixUnitaire + "}";
    }
}
