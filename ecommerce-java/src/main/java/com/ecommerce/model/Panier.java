
package com.ecommerce.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Panier implements Iterable<LignePanier> {

    private Long id;
    private final List<LignePanier> lignes;

    public Panier(Long id) {
        this.id = id;
        this.lignes = new ArrayList<>();
    }

    public void ajouterProduit(Produit produit, int quantite) {
        Objects.requireNonNull(produit, "Produit obligatoire");

        if (quantite <= 0) {
            throw new IllegalArgumentException("Quantite invalide");
        }

        for (LignePanier ligne : lignes) {
            if (ligne.getProduit().equals(produit)) {
                ligne.modifierQuantite(ligne.getQuantite() + quantite);
                return;
            }
        }

        lignes.add(new LignePanier(produit, quantite));
    }

    public void retirerProduit(Produit produit) {
        lignes.removeIf(ligne -> ligne.getProduit().equals(produit));
    }

    public void modifierQuantite(Produit produit, int quantite) {
        Objects.requireNonNull(produit, "Produit obligatoire");

        for (LignePanier ligne : lignes) {
            if (ligne.getProduit().equals(produit)) {
                ligne.modifierQuantite(quantite);
                return;
            }
        }

        throw new IllegalArgumentException("Produit introuvable dans le panier");
    }

    public BigDecimal calculerTotal() {
        return lignes.stream()
                .map(LignePanier::calculerSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void vider() {
        lignes.clear();
    }

    public boolean estVide() {
        return lignes.isEmpty();
    }

    public int getNombreArticles() {
        return lignes.stream()
                .mapToInt(LignePanier::getQuantite)
                .sum();
    }

    public Long getId() {
        return id;
    }

    public List<LignePanier> getLignes() {
        return Collections.unmodifiableList(lignes);
    }

    @Override
    public Iterator<LignePanier> iterator() {
        return new PanierIterator();
    }

    private class PanierIterator implements Iterator<LignePanier> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < lignes.size();
        }

        @Override
        public LignePanier next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            return lignes.get(index++);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Panier panier)) return false;
        return id != null && Objects.equals(id, panier.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id=" + id +
                ", lignes=" + lignes +
                ", total=" + calculerTotal() +
                '}';
    }
}
