package com.ecommerce.service;

import com.ecommerce.enums.ModePaiement;
import com.ecommerce.enums.StatutCommande;
import com.ecommerce.model.Commande;
import com.ecommerce.model.Paiement;

import java.util.concurrent.atomic.AtomicLong;

public class PaiementService {
    private final AtomicLong sequence = new AtomicLong(1);

    public Paiement payer(Commande commande, ModePaiement mode) {
        Paiement paiement = new Paiement(sequence.getAndIncrement(), commande.getMontantTotal(), mode);
        commande.setPaiement(paiement);
        if (paiement.effectuerPaiement()) {
            commande.changerStatut(StatutCommande.PAYEE);
        }
        return paiement;
    }

    public boolean rembourser(Commande commande) {
        Paiement paiement = commande.getPaiement();
        return paiement != null && paiement.rembourser();
    }
}
