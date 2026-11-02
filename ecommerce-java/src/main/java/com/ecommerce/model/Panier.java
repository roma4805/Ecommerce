package com.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Panier {
    private Long id;
    private LocalDateTime dateCreation;
    private final List<LignePanier> lignes = new ArrayList<>();

    public Panier(Long id) {
        this.id = id;
        this.dateCreation = LocalDateTime.now();
    }

    public void ajouterProduit(Produit p, int qte) {
        if (qte <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive");
        }
        lignes.stream()
                .filter(l -> l.getProduit().equals(p))
                .findFirst()
                .ifPresentOrElse(
                        l -> l.modifierQuantite(l.getQuantite() + qte),
                        () -> lignes.add(new LignePanier(p, qte)));
    }

    public void retirerProduit(Produit p) {
        lignes.removeIf(l -> l.getProduit().equals(p));
    }

    public void modifierQuantite(Produit p, int qte) {
        if (qte <= 0) {
            retirerProduit(p);
            return;
        }
        lignes.stream()
                .filter(l -> l.getProduit().equals(p))
                .findFirst()
                .ifPresent(l -> l.modifierQuantite(qte));
    }

    public BigDecimal calculerTotal() {
        return lignes.stream()
                .map(LignePanier::calculerSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void vider() {
        lignes.clear();
    }

    public Long getId() { return id; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public List<LignePanier> getLignes() { return Collections.unmodifiableList(lignes); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Panier)) return false;
        Panier p = (Panier) o;
        return Objects.equals(id, p.id) && Objects.equals(dateCreation, p.dateCreation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateCreation);
    }

    @Override
    public String toString() {
        return "Panier{id=" + id + ", dateCreation=" + dateCreation
                + ", nbLignes=" + lignes.size() + ", total=" + calculerTotal() + "}";
    }
}
