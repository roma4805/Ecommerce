package com.ecommerce.model;

import com.ecommerce.enums.StatutLivraison;

import java.time.LocalDate;
import java.util.Objects;

public class Livraison {
    private Long id;
    private String numeroSuivi;
    private LocalDate dateExpedition;
    private LocalDate dateLivraison;
    private StatutLivraison statut;

    public Livraison(Long id) {
        this.id = id;
        this.numeroSuivi = String.format("TRK-%06d", id);
        this.statut = StatutLivraison.EN_PREPARATION;
    }

    public void expedier() {
        this.dateExpedition = LocalDate.now();
        this.statut = StatutLivraison.EXPEDIEE;
    }

    public void mettreAJourStatut(StatutLivraison s) {
        this.statut = s;
        if (s == StatutLivraison.LIVREE) {
            this.dateLivraison = LocalDate.now();
        }
    }

    public String suivreLivraison() {
        return "Colis " + numeroSuivi + " : " + statut;
    }

    public Long getId() { return id; }
    public String getNumeroSuivi() { return numeroSuivi; }
    public LocalDate getDateExpedition() { return dateExpedition; }
    public LocalDate getDateLivraison() { return dateLivraison; }
    public StatutLivraison getStatut() { return statut; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Livraison)) return false;
        Livraison l = (Livraison) o;
        return Objects.equals(id, l.id) && Objects.equals(numeroSuivi, l.numeroSuivi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroSuivi);
    }

    @Override
    public String toString() {
        return "Livraison{id=" + id + ", numeroSuivi='" + numeroSuivi + "', dateExpedition="
                + dateExpedition + ", dateLivraison=" + dateLivraison + ", statut=" + statut + "}";
    }
}
