package com.ecommerce.model;

import com.ecommerce.repository.CategorieRepository;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.ProduitRepository;

import java.util.List;
import java.util.Objects;

public class Administrateur extends Utilisateur {
    private String niveauAcces;

    public Administrateur(Long id, String nom, String prenom, String email,
                          String motDePasse, String telephone) {
        super(id, nom, prenom, email, motDePasse, telephone);
        this.niveauAcces = "ADMIN";
    }

    public void ajouterProduit(Produit p, ProduitRepository repo) {
        repo.save(p);
    }

    public void modifierProduit(Produit p, ProduitRepository repo) {
        repo.save(p);
    }

    public void supprimerProduit(Long id, ProduitRepository repo) {
        repo.deleteById(id);
    }

    public void gererCategories(Categorie c, CategorieRepository repo) {
        repo.save(c);
    }

    public List<Commande> consulterCommandes(CommandeRepository repo) {
        return repo.findAll();
    }

    public String getNiveauAcces() { return niveauAcces; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Administrateur a = (Administrateur) o;
        return Objects.equals(niveauAcces, a.niveauAcces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), niveauAcces);
    }

    @Override
    public String toString() {
        return "Administrateur{id=" + id + ", nom='" + nom + "', prenom='" + prenom
                + "', email='" + email + "', niveauAcces='" + niveauAcces + "'}";
    }
}
