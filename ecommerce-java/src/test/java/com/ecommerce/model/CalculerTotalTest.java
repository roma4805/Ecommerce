package com.ecommerce.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculerTotalTest {
    @Test
    void panierAdditionneLesSousTotauxEtFusionneLesProduits() {
        Produit produit = new Produit(1L, "Clavier", "", new BigDecimal("12.50"), 10, null);
        Panier panier = new Panier(1L);

        panier.ajouterProduit(produit, 2);
        panier.ajouterProduit(produit, 3);

        assertEquals(1, panier.getLignes().size());
        assertEquals(5, panier.getLignes().get(0).getQuantite());
        assertEquals(new BigDecimal("62.50"), panier.calculerTotal());
    }

    @Test
    void commandeCalculeLeTotalDesLignes() {
        Client client = creerClient();
        Produit produit = new Produit(1L, "Clavier", "", new BigDecimal("12.50"), 10, null);
        Commande commande = new Commande.Builder(client)
                .ajouterLigne(produit, 2)
                .ajouterLigne(produit, 1)
                .build();

        assertEquals(new BigDecimal("37.50"), commande.calculerTotal());
    }

    @Test
    void builderExigeUnClient() {
        assertThrows(IllegalArgumentException.class, () -> new Commande.Builder(null));
    }

    private static Client creerClient() {
        return new Client(1L, "Benali", "Youssef", "youssef@mail.ma",
                "secret99", "0611223344");
    }
}
