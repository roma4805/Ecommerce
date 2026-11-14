package com.ecommerce.model;

import com.ecommerce.exception.StockInsuffisantException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * final : personne ne peut heriter de Client (obligatoire pour une sous-classe d'une
 * classe sealed : final, sealed ou non-sealed).
 */
public final class Client extends Utilisateur {

    @Serial
    private static final long serialVersionUID = 1L;

    private final LocalDate dateInscription;
    private final List<Adresse> adresses = new ArrayList<>();
    private final List<Avis> avisRediges = new ArrayList<>();
    /** transient : les commandes sont sauvegardees par leur propre repository (Dev 2 / Dev 3). */
    private transient List<Commande> commandes = new ArrayList<>();
    /** transient : le panier est temporaire, il est recree vide apres rechargement. */
    private transient Panier panier;

    public Client(Long id, String nom, String prenom, String email,
                  String motDePasse, String telephone) {
        super(id, nom, prenom, email, motDePasse, telephone);
        this.dateInscription = LocalDate.now();
        this.panier = new Panier(id);
    }

    /** Redefinition de la methode abstraite de Utilisateur. */
    @Override
    public String getRole() {
        return "CLIENT";
    }

    public List<Produit> consulterProduits(List<Produit> catalogue) {
        return catalogue.stream()
                .filter(p -> FiltreProduit.disponibles().accepter(p))
                .collect(Collectors.toList());
    }

    public void ajouterAuPanier(Produit p, int quantite) {
        if (!p.isActif()) {
            throw new IllegalArgumentException("Produit inactif : " + p.getNom());
        }
        if (!p.verifierStock(quantite)) {
            throw new StockInsuffisantException(p.getNom(), quantite, p.getStock());
        }
        panier.ajouterProduit(p, quantite);
    }

    public Commande passerCommande() {
        if (panier.getLignes().isEmpty()) {
            throw new IllegalStateException("Le panier est vide");
        }
        panier.getLignes().stream()
                .filter(l -> !l.getProduit().verifierStock(l.getQuantite()))
                .findFirst()
                .ifPresent(l -> {
                    throw new StockInsuffisantException(l.getProduit().getNom(),
                            l.getQuantite(), l.getProduit().getStock());
                });
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

    // ---- SURCHARGE : meme nom, parametres differents ----

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

    public void ajouterAdresse(Adresse a) {
        adresses.add(Objects.requireNonNull(a, "adresse"));
    }

    public LocalDate getDateInscription() { return dateInscription; }
    public List<Adresse> getAdresses() { return Collections.unmodifiableList(adresses); }
    public List<Avis> getAvisRediges() { return Collections.unmodifiableList(avisRediges); }
    public Panier getPanier() { return panier; }

    /**
     * Appelee automatiquement pendant la deserialisation.
     * Les champs transient valent null : on les recree.
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.commandes = new ArrayList<>();
        this.panier = new Panier(getId());
    }

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
        return "Client{id=" + getId() + ", nom='" + getNom() + "', prenom='" + getPrenom()
                + "', email='" + getEmail() + "', dateInscription=" + dateInscription
                + ", nbCommandes=" + commandes.size() + "}";
    }
}
