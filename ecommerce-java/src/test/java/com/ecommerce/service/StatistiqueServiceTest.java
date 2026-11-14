package com.ecommerce.service;

import com.ecommerce.enums.StatutCommande;
import com.ecommerce.model.Categorie;
import com.ecommerce.model.Client;
import com.ecommerce.model.Commande;
import com.ecommerce.model.Produit;
import com.ecommerce.repository.ClientRepository;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatistiqueServiceTest {
    private ProduitRepository produitRepo;
    private CommandeRepository commandeRepo;
    private Client client;
    private Produit clavier;
    private StatistiqueService statistiques;
    private CommandeService commandes;

    @BeforeEach
    void setUp() {
        produitRepo = new ProduitRepository();
        ClientRepository clientRepo = new ClientRepository();
        commandeRepo = new CommandeRepository();
        client = new Client(1L, "Benali", "Youssef", "youssef@mail.ma",
                "secret99", "0611223344");
        clientRepo.save(client);
        Categorie informatique = new Categorie(1L, "Informatique", "");
        clavier = new Produit(1L, "Clavier", "", new BigDecimal("300"), 4, informatique);
        Produit souris = new Produit(2L, "Souris", "", new BigDecimal("250"), 0, informatique);
        produitRepo.saveAll(List.of(clavier, souris));
        commandeRepo.save(creerCommande(clavier, 2));
        Commande commandeConfirmee = creerCommande(souris, 1);
        commandeConfirmee.confirmer();
        commandeRepo.save(commandeConfirmee);
        Commande commandeAnnulee = creerCommande(clavier, 1);
        commandeAnnulee.changerStatut(StatutCommande.ANNULEE);
        commandeRepo.save(commandeAnnulee);
        commandes = new CommandeService(commandeRepo);
        statistiques = new StatistiqueService(produitRepo, clientRepo, commandeRepo);
    }

    @Test
    void chiffreAffairesEtRegroupementsSontCorrects() {
        assertEquals(new BigDecimal("850"), statistiques.chiffreAffaires());
        assertEquals(2, statistiques.produitsDistinctsCommandes().size());
        assertEquals(new BigDecimal("850"), commandes.totalParClient().get(client));
        assertEquals(1, commandes.commandesParStatut().get(StatutCommande.EN_ATTENTE).size());
        assertEquals(1, commandes.commandesParStatut().get(StatutCommande.CONFIRMEE).size());
        assertEquals(1, commandes.commandesParStatut().get(StatutCommande.ANNULEE).size());
    }

    @Test
    void statistiquesExposentLesOperationsDeStreamsAttendues() {
        assertEquals(1, statistiques.commandesPaginees(1, 1).size());
        assertThrows(IllegalArgumentException.class, () -> statistiques.commandesPaginees(-1, 1));
        Map<Boolean, List<Commande>> repartition = statistiques.commandesParSeuilMontant();
        assertEquals(1, repartition.get(true).size());
        assertEquals(2, repartition.get(false).size());
        assertEquals(2, statistiques.stocksParNomProduit().size());
        assertEquals(3, statistiques.commandesCompteesParStatut().size());
        assertEquals(2.0, statistiques.stockMoyen());
        assertTrue(statistiques.existeProduitEnRupture());
        assertTrue(statistiques.tousLesPrixPositifs());
        assertTrue(statistiques.aucunProduitSansNom());
        assertEquals("Souris", statistiques.produitLeMoinsCher().orElseThrow().getNom());
        assertEquals("Clavier", statistiques.produitLePlusCher().orElseThrow().getNom());
        assertTrue(statistiques.premiereCommande().isPresent());
        assertFalse(statistiques.rapportVentes().isEmpty());
    }

    @Test
    void categoriesAcheteesSontDistinctesEtTriees() {
        Categorie categorieEquivalent = new Categorie(1L, "Informatique", "Autre description");
        Produit tapisSouris = new Produit(3L, "Tapis de souris", "",
                new BigDecimal("20"), 5, categorieEquivalent);
        client.ajouterAuPanier(clavier, 1);
        client.ajouterAuPanier(tapisSouris, 1);
        client.passerCommande();

        assertEquals(clavier.getCategorie(), categorieEquivalent);
        assertEquals(clavier.getCategorie().hashCode(), categorieEquivalent.hashCode());
        assertEquals(1, client.getCategoriesAchetees().size());
        assertEquals("Informatique", client.getCategoriesAcheteesTriees().first().getNom());
    }

    private Commande creerCommande(Produit produit, int quantite) {
        return new Commande.Builder(client).ajouterLigne(produit, quantite).build();
    }
}
