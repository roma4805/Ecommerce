package com.ecommerce.service;

import com.ecommerce.enums.StatutCommande;
import com.ecommerce.enums.StatutLivraison;
import com.ecommerce.model.Commande;
import com.ecommerce.model.Livraison;

import java.util.concurrent.atomic.AtomicLong;

public class LivraisonService {
    private final AtomicLong sequence = new AtomicLong(1);

    public Livraison planifier(Commande commande) {
        Livraison livraison = new Livraison(sequence.getAndIncrement());
        commande.setLivraison(livraison);
        return livraison;
    }

    public void expedier(Commande commande) {
        commande.getLivraison().expedier();
        commande.changerStatut(StatutCommande.EXPEDIEE);
    }

    public void livrer(Commande commande) {
        commande.getLivraison().mettreAJourStatut(StatutLivraison.LIVREE);
        commande.changerStatut(StatutCommande.LIVREE);
    }
}
