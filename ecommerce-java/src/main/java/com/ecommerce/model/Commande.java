package com.ecommerce.model;

import com.ecommerce.enums.StatutCommande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Commande {
    private static final AtomicLong SEQ = new AtomicLong(1);

    private Long id;
    private LocalDateTime dateCommande;
    private StatutCommande statut;
    private BigDecimal montantTotal;
    private final Client client;
    private final List<LigneCommande> lignes;
    private Livraison livraison;
    private Paiement paiement;

    private Commande(Builder b) {
        this.id = b.id;
        this.client = b.client;
        this.lignes = new ArrayList<>(b.lignes);
        this.dateCommande = LocalDateTime.now();
        this.statut = StatutCommande.EN_ATTENTE;
        calculerTotal();
    }

    public static Long nextId() {
        return SEQ.getAndIncrement();
    }

    public BigDecimal calculerTotal() {
        montantTotal = lignes.stream()
                .map(LigneCommande::calculerSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return montantTotal;
    }

    public void confirmer() {
        if (statut != StatutCommande.EN_ATTENTE) {
            throw new IllegalStateException("Seule une commande EN_ATTENTE peut etre confirmee");
        }
        statut = StatutCommande.CONFIRMEE;
    }

    public void annuler() {
        if (statut == StatutCommande.EXPEDIEE || statut == StatutCommande.LIVREE
                || statut == StatutCommande.ANNULEE) {
            throw new IllegalStateException("Commande non annulable : " + statut);
        }
        lignes.forEach(l -> l.getProduit().augmenterStock(l.getQuantite()));
        statut = StatutCommande.ANNULEE;
    }

    public void changerStatut(StatutCommande s) {
        if (statut == StatutCommande.ANNULEE || statut == StatutCommande.LIVREE) {
            throw new IllegalStateException("Statut final deja atteint : " + statut);
        }
        this.statut = s;
    }

    public Long getId() { return id; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public StatutCommande getStatut() { return statut; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public Client getClient() { return client; }
    public List<LigneCommande> getLignes() { return Collections.unmodifiableList(lignes); }
    public Livraison getLivraison() { return livraison; }
    public void setLivraison(Livraison livraison) { this.livraison = livraison; }
    public Paiement getPaiement() { return paiement; }
    public void setPaiement(Paiement paiement) { this.paiement = paiement; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commande)) return false;
        Commande c = (Commande) o;
        return Objects.equals(id, c.id) && Objects.equals(dateCommande, c.dateCommande);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateCommande);
    }

    @Override
    public String toString() {
        return "Commande{id=" + id + ", dateCommande=" + dateCommande + ", statut=" + statut
                + ", montantTotal=" + montantTotal + ", clientId=" + client.getId()
                + ", nbLignes=" + lignes.size() + "}";
    }

    /** Classe imbriquee statique : patron Builder pour construire une Commande. */
    public static class Builder {
        private Long id = nextId();
        private final Client client;
        private final List<LigneCommande> lignes = new ArrayList<>();

        public Builder(Client client) {
            if (client == null) {
                throw new IllegalArgumentException("Le client est obligatoire");
            }
            this.client = client;
        }

        public Builder ajouterLigne(Produit p, int quantite) {
            lignes.add(new LigneCommande(p, quantite));
            return this;
        }

        public Commande build() {
            if (lignes.isEmpty()) {
                throw new IllegalStateException("Une commande doit avoir au moins une ligne");
            }
            return new Commande(this);
        }
    }
}
