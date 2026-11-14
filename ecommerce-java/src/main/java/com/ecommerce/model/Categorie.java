package com.ecommerce.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Categorie implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private String nom;
    private String description;
    private final List<Produit> produits = new ArrayList<>();

    public Categorie(Long id, String nom, String description) {
        this.id = Objects.requireNonNull(id, "id");
        setNom(nom);
        setDescription(description);
    }

    public void ajouterProduit(Produit p) {
        if (!produits.contains(p)) {
            produits.add(p);
            p.setCategorie(this);
        }
    }

    public void retirerProduit(Produit p) {
        if (produits.remove(p)) {
            p.setCategorie(null);
        }
    }

    public List<Produit> listerProduits() {
        return Collections.unmodifiableList(produits);
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }

    public void setNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom de la categorie est obligatoire");
        }
        this.nom = nom.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categorie c)) return false;
        return Objects.equals(id, c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Categorie{id=" + id + ", nom='" + nom + "', description='" + description
                + "', nbProduits=" + produits.size() + "}";
    }
}
