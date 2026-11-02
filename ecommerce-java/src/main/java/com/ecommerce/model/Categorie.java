package com.ecommerce.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Categorie {
    private Long id;
    private String nom;
    private String description;
    private final List<Produit> produits = new ArrayList<>();

    public Categorie(Long id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public void ajouterProduit(Produit p) {
        if (!produits.contains(p)) {
            produits.add(p);
            p.setCategorie(this);
        }
    }

    public void retirerProduit(Produit p) {
        produits.remove(p);
    }

    public List<Produit> listerProduits() {
        return Collections.unmodifiableList(produits);
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categorie)) return false;
        Categorie c = (Categorie) o;
        return Objects.equals(id, c.id) && Objects.equals(nom, c.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom);
    }

    @Override
    public String toString() {
        return "Categorie{id=" + id + ", nom='" + nom + "', description='" + description
                + "', nbProduits=" + produits.size() + "}";
    }
}
