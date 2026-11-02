package com.ecommerce.model;

import com.ecommerce.enums.ModePaiement;
import com.ecommerce.enums.StatutPaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Paiement {
    private Long id;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private ModePaiement mode;
    private StatutPaiement statut;
    private String reference;

    public Paiement(Long id, BigDecimal montant, ModePaiement mode) {
        this.id = id;
        this.montant = montant;
        this.mode = mode;
        this.statut = StatutPaiement.EN_ATTENTE;
        this.reference = String.format("PAY-%06d", id);
    }

    public boolean effectuerPaiement() {
        if (statut == StatutPaiement.REUSSI) {
            return false;
        }
        if (montant != null && montant.signum() > 0) {
            statut = StatutPaiement.REUSSI;
            datePaiement = LocalDateTime.now();
            return true;
        }
        statut = StatutPaiement.ECHOUE;
        return false;
    }

    public boolean rembourser() {
        if (statut != StatutPaiement.REUSSI) {
            return false;
        }
        statut = StatutPaiement.REMBOURSE;
        return true;
    }

    public StatutPaiement verifierStatut() {
        return statut;
    }

    public Long getId() { return id; }
    public BigDecimal getMontant() { return montant; }
    public LocalDateTime getDatePaiement() { return datePaiement; }
    public ModePaiement getMode() { return mode; }
    public StatutPaiement getStatut() { return statut; }
    public String getReference() { return reference; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paiement)) return false;
        Paiement p = (Paiement) o;
        return Objects.equals(id, p.id) && Objects.equals(reference, p.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference);
    }

    @Override
    public String toString() {
        return "Paiement{id=" + id + ", montant=" + montant + ", datePaiement=" + datePaiement
                + ", mode=" + mode + ", statut=" + statut + ", reference='" + reference + "'}";
    }
}
