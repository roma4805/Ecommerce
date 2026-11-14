package com.ecommerce.service;

import com.ecommerce.model.Client;
import com.ecommerce.model.LignePanier;
import com.ecommerce.model.Produit;
import com.ecommerce.exception.StockInsuffisantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanierServiceTest {
    private final PanierService service = new PanierService();
    private Client client;
    private Produit produit;

    @BeforeEach
    void setUp() {
        client = new Client(1L, "Benali", "Youssef", "youssef@mail.ma",
                "secret99", "0611223344");
        produit = new Produit(1L, "Clavier", "", new BigDecimal("12.50"), 5, null);
    }

    @Test
    void ajouteEtConsulteLesLignesDuPanier() {
        service.ajouter(client, produit, 2);
        service.ajouter(client, produit, 1);

        List<LignePanier> lignes = service.consulter(client);
        assertEquals(1, lignes.size());
        assertEquals(3, lignes.get(0).getQuantite());
        assertEquals(new BigDecimal("37.50"), lignes.get(0).calculerSousTotal());
        assertEquals(3, service.nombreArticles(client));
        assertEquals(new BigDecimal("37.50"), service.total(client));
        assertThrows(UnsupportedOperationException.class, lignes::clear);
    }

    @Test
    void modifierRetirerEtViderLePanierRespectentLesReglesMetier() {
        service.ajouter(client, produit, 2);
        service.changerQuantite(client, produit, 4);
        assertEquals(4, service.consulter(client).get(0).getQuantite());

        assertThrows(IllegalArgumentException.class,
                () -> service.changerQuantite(client, produit, 0));
        assertThrows(StockInsuffisantException.class,
                () -> service.ajouter(client, produit, 2));

        service.retirer(client, produit);
        assertTrue(service.estVide(client));
        service.ajouter(client, produit, 1);
        service.vider(client);
        assertTrue(service.consulter(client).isEmpty());
    }

    @Test
    void exigeUnClientEtUnProduit() {
        assertThrows(NullPointerException.class, () -> service.consulter(null));
        assertThrows(NullPointerException.class, () -> service.ajouter(client, null, 1));
        assertTrue(service.estVide(client));
    }
}
