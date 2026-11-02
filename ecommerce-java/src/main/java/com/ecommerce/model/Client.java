package com.ecommerce.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Client extends Utilisateur {
    private LocalDate dateInscription;
    private final List<Adresse> adresses = new ArrayList<>();
    private final List<Commande> commandes = new ArrayList<>();
    private final List<Avis> avisRediges = new ArrayList<>();
    private final Panier panier;

    public Client(Long id, String nom, String prenom, String email,
                  String motDePasse, String telephone) {
        super(id, nom, prenom, email, motDePasse, telephone);
        this.dateInscription = LocalDate.now();
        this.panier = new Panier(id);
    }

    public List<Produit> consulterProduits(List<Produit> catalogue) {
        return catalogue.stream()
                .filter(p -> p.isActif() && p.getStock() > 0)
                .collect(Collectors.toList());
    }

    public void ajouterAuPanier(Produit p, int quantite) {
        if (!p.isActif() || !p.verifierStock(quantite)) {
            throw new IllegalArgumentException("Produit indisponible : " + p.getNom());
        }
        panier.ajouterProduit(p, quantite);
    }

    public Commande passerCommande() {
        if (panier.getLignes().isEmpty()) {
            throw new IllegalStateException("Le panier est vide");
        }
        boolean stockOk = panier.getLignes().stream()
                .allMatch(l -> l.getProduit().verifierStock(l.getQuantite()));
        if (!stockOk) {
            throw new IllegalStateException("Stock insuffisant pour au moins un produit");
        }
        Commande.Builder builder = new Commande.Builder(this);
        for (LignePanier l : panier.getLignes()) {
            l.getProduit().diminuerStock(l.getQuantite());
            builder.ajouterLigne(l.getProduit(), l.getQuantite());
        }
        Commande commande = builder.build();
        commandes.add(commande);
        panier.vider();
        return commande;
    }

    public Avis laisserAvis(Produit p, int note) {
        return laisserAvis(p, note, "");
    }

    public Avis laisserAvis(Produit p, int note, String commentaire) {
        Avis avis = new Avis(Avis.nextId(), note, commentaire, this, p);
        avis.ajouterAvis();
        avisRediges.add(avis);
        return avis;
    }

    public List<Commande> consulterCommandes() {
        return Collections.unmodifiableList(commandes);
    }

    public void ajouterAdresse(Adresse a) { adresses.add(a); }

    public LocalDate getDateInscription() { return dateInscription; }
    public List<Adresse> getAdresses() { return Collections.unmodifiableList(adresses); }
    public List<Avis> getAvisRediges() { return Collections.unmodifiableList(avisRediges); }
    public Panier getPanier() { return panier; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Client c = (Client) o;
        return Objects.equals(dateInscription, c.dateInscription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateInscription);
    }

    @Override
    public String toString() {
        return "Client{id=" + id + ", nom='" + nom + "', prenom='" + prenom
                + "', email='" + email + "', dateInscription=" + dateInscription
                + ", nbCommandes=" + commandes.size() + "}";
    }
}
