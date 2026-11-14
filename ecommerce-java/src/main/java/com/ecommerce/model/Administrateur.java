package com.ecommerce.model;

import com.ecommerce.exception.ProduitIntrouvableException;
import com.ecommerce.repository.CategorieRepository;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.ProduitRepository;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/** final : obligatoire pour une sous-classe d'une classe sealed (ou sealed / non-sealed). */
public final class Administrateur extends Utilisateur {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String niveauAcces;

    public Administrateur(Long id, String nom, String prenom, String email,
                          String motDePasse, String telephone) {
        super(id, nom, prenom, email, motDePasse, telephone);
        this.niveauAcces = "ADMIN";
    }

    /** Redefinition de la methode abstraite de Utilisateur. */
    @Override
    public String getRole() {
        return "ADMINISTRATEUR";
    }

    // ---- SURCHARGE de ajouterProduit : deux signatures differentes ----

    /** Version 1 : on passe un produit deja construit. */
    public void ajouterProduit(Produit p, ProduitRepository repo) {
        repo.save(p);
    }

    /** Version 2 : on passe les informations, l'admin construit le produit. */
    public Produit ajouterProduit(String nom, String description, BigDecimal prix, int stock,
                                  Categorie categorie, ProduitRepository repo) {
        Long nouvelId = repo.findAll().stream().mapToLong(Produit::getId).max().orElse(0) + 1;
        Produit p = new Produit(nouvelId, nom, description, prix, stock, categorie);
        repo.save(p);
        return p;
    }

    public void modifierProduit(Produit p, ProduitRepository repo) throws ProduitIntrouvableException {
        if (repo.findById(p.getId()).isEmpty()) {
            throw new ProduitIntrouvableException(p.getId());
        }
        repo.save(p);
    }

    public void supprimerProduit(Long id, ProduitRepository repo) throws ProduitIntrouvableException {
        if (repo.findById(id).isEmpty()) {
            throw new ProduitIntrouvableException(id);
        }
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
        return "Administrateur{id=" + getId() + ", nom='" + getNom() + "', prenom='" + getPrenom()
                + "', email='" + getEmail() + "', niveauAcces='" + niveauAcces + "'}";
    }
}
