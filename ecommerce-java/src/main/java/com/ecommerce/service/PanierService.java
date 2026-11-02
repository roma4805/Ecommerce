package com.ecommerce.service;

import com.ecommerce.model.Client;
import com.ecommerce.model.Panier;
import com.ecommerce.model.Produit;

import java.math.BigDecimal;

public class PanierService {

    public void ajouter(Client client, Produit produit, int quantite) {
        client.ajouterAuPanier(produit, quantite);
    }

    public void retirer(Client client, Produit produit) {
        client.getPanier().retirerProduit(produit);
    }

    public void changerQuantite(Client client, Produit produit, int quantite) {
        client.getPanier().modifierQuantite(produit, quantite);
    }

    public BigDecimal total(Client client) {
        return client.getPanier().calculerTotal();
    }

    public void afficher(Client client) {
        Panier panier = client.getPanier();
        panier.getLignes().forEach(l -> System.out.println("  - " + l));
        System.out.println("  Total panier : " + panier.calculerTotal());
    }
}
