package com.ecommerce.model;

import com.ecommerce.exception.StockInsuffisantException;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Produit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private int stock;
    private boolean actif;
    private Categorie categorie;
    private final List<Avis> avis = new ArrayList<>();

    public Produit(Long id, String nom, String description, BigDecimal prix,
                   int stock, Categorie categorie) {
        this.id = Objects.requireNonNull(id, "id");
        setNom(nom);
        setDescription(description);
        modifierPrix(prix);
        if (stock < 0) {
            throw new IllegalArgumentException("Le stock ne peut pas etre negatif");
        }
        this.stock = stock;
        this.actif = true;
        if (categorie != null) {
            categorie.ajouterProduit(this);
        }
    }

    public void modifierPrix(BigDecimal prix) {
        if (prix == null || prix.signum() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas etre negatif");
        }
        this.prix = prix;
    }

    public boolean verifierStock(int qte) {
        return qte > 0 && stock >= qte;
    }

    /** Lance une exception UNCHECKED si le stock est insuffisant. */
    public void diminuerStock(int qte) {
        if (qte <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive");
        }
        if (stock < qte) {
            throw new StockInsuffisantException(nom, qte, stock);
        }
        stock -= qte;
    }

    public void augmenterStock(int qte) {
        if (qte <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive");
        }
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
    public Categorie getCategorie() { return categorie; }
    public List<Avis> getAvis() { return Collections.unmodifiableList(avis); }

    public void setNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        this.nom = nom.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public void setActif(boolean actif) { this.actif = actif; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produit p)) return false;   // pattern matching
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
