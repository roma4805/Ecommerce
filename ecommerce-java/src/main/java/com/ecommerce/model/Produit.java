package com.ecommerce.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Produit {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private int stock;
    private boolean actif;
    private Categorie categorie;
    private final List<Avis> avis = new ArrayList<>();

    public Produit(Long id, String nom, String description, BigDecimal prix,
                   int stock, Categorie categorie) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
        this.actif = true;
        if (categorie != null) {
            categorie.ajouterProduit(this);
        }
    }

    public void modifierPrix(BigDecimal prix) {
        if (prix.signum() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas etre negatif");
        }
        this.prix = prix;
    }

    public boolean verifierStock(int qte) {
        return stock >= qte;
    }

    public void diminuerStock(int qte) {
        if (!verifierStock(qte)) {
            throw new IllegalStateException("Stock insuffisant pour " + nom);
        }
        stock -= qte;
    }

    public void augmenterStock(int qte) {
        stock += qte;
    }

    public void ajouterAvis(Avis a) { avis.add(a); }
    public void retirerAvis(Avis a) { avis.remove(a); }

    public double getNoteMoyenne() {
        return avis.stream().mapToInt(Avis::getNote).average().orElse(0.0);
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public BigDecimal getPrix() { return prix; }
    public int getStock() { return stock; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
    public List<Avis> getAvis() { return Collections.unmodifiableList(avis); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produit)) return false;
        Produit p = (Produit) o;
        return Objects.equals(id, p.id) && Objects.equals(nom, p.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom);
    }

    @Override
    public String toString() {
        return "Produit{id=" + id + ", nom='" + nom + "', prix=" + prix
                + ", stock=" + stock + ", actif=" + actif + "}";
    }
}
