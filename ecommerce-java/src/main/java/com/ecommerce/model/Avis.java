package com.ecommerce.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Avis implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** static : partage par tous les avis, jamais serialise. */
    private static final AtomicLong SEQ = new AtomicLong(1);

    private Long id;
    private int note;
    private String commentaire;
    private LocalDate dateAvis;
    private final Client client;
    private final Produit produit;

    public Avis(Long id, int note, String commentaire, Client client, Produit produit) {
        verifierNote(note);
        this.id = id;
        this.note = note;
        this.commentaire = commentaire == null ? "" : commentaire.trim();
        this.dateAvis = LocalDate.now();
        this.client = client;
        this.produit = produit;
    }

    public static Long nextId() {
        return SEQ.getAndIncrement();
    }

    private static void verifierNote(int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit etre entre 1 et 5");
        }
    }

    public void ajouterAvis() {
        produit.ajouterAvis(this);
    }

    public void modifierAvis(int note, String commentaire) {
        verifierNote(note);
        this.note = note;
        this.commentaire = commentaire;
        this.dateAvis = LocalDate.now();
    }

    public void supprimerAvis() {
        produit.retirerAvis(this);
    }

    public Long getId() { return id; }
    public int getNote() { return note; }
    public String getCommentaire() { return commentaire; }
    public LocalDate getDateAvis() { return dateAvis; }
    public Client getClient() { return client; }
    public Produit getProduit() { return produit; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Avis a)) return false;
        return Objects.equals(id, a.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Avis{id=" + id + ", note=" + note + ", commentaire='" + commentaire
                + "', dateAvis=" + dateAvis + ", produit=" + produit.getNom() + "}";
    }
}
