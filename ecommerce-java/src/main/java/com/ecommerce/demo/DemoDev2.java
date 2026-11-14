package com.ecommerce.demo;

import com.ecommerce.enums.StatutCommande;
import com.ecommerce.model.Categorie;
import com.ecommerce.model.Client;
import com.ecommerce.model.Commande;
import com.ecommerce.model.LignePanier;
import com.ecommerce.model.Panier;
import com.ecommerce.model.Produit;
import com.ecommerce.repository.ClientRepository;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.ProduitRepository;
import com.ecommerce.service.CommandeService;
import com.ecommerce.service.StatistiqueService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DemoDev2 {
    public static void main(String[] args) {
        Categorie informatique = new Categorie(1L, "Informatique", "Accessoires");
        Categorie livres = new Categorie(2L, "Livres", "Livres et guides");
        Produit clavier = new Produit(1L, "Clavier", "Mecanique",
                new BigDecimal("450.00"), 8, informatique);
        Produit souris = new Produit(2L, "Souris", "Sans fil",
                new BigDecimal("150.00"), 12, informatique);
        Produit guide = new Produit(3L, "Guide Java", "Programmation",
                new BigDecimal("80.00"), 5, livres);

        Panier panier = new Panier(1L);
        panier.ajouterProduit(clavier, 1);
        panier.ajouterProduit(clavier, 2);
        System.out.println("Panier fusionne :");
        for (LignePanier ligne : panier) {
            System.out.println("  " + ligne);
        }
        System.out.println("Total panier : " + panier.calculerTotal());

        Client client = new Client(1L, "Benali", "Youssef",
                "youssef@mail.ma", "secret99", "0611223344");
        Commande premiere = new Commande.Builder(client)
                .ajouterLigne(clavier, 1)
                .ajouterLigne(souris, 1)
                .build();
        Commande seconde = new Commande.Builder(client)
                .ajouterLigne(guide, 2)
                .build();
        seconde.confirmer();
        System.out.println("Builder / total commande : " + premiere.calculerTotal());

        Comparator<Commande> comparateurAnonyme = new Comparator<>() {
            @Override
            public int compare(Commande gauche, Commande droite) {
                int comparaisonMontants = gauche.getMontantTotal()
                        .compareTo(droite.getMontantTotal());
                return comparaisonMontants != 0 ? comparaisonMontants
                        : gauche.getDateCommande().compareTo(droite.getDateCommande());
            }
        };
        Comparator<Commande> comparateurLambda = Comparator
                .comparing(Commande::getMontantTotal)
                .thenComparing(Commande::getDateCommande);
        List<Commande> triAnonyme = new ArrayList<>(List.of(premiere, seconde));
        List<Commande> triLambda = new ArrayList<>(List.of(premiere, seconde));
        triAnonyme.sort(comparateurAnonyme);
        triLambda.sort(comparateurLambda);
        System.out.println("Comparateur anonyme == lambda : " + triAnonyme.equals(triLambda));

        client.ajouterAuPanier(clavier, 1);
        Commande commandeDepuisPanier = client.passerCommande();

        ProduitRepository produitRepo = new ProduitRepository();
        produitRepo.saveAll(List.of(clavier, souris, guide));
        ClientRepository clientRepo = new ClientRepository();
        clientRepo.save(client);
        CommandeRepository commandeRepo = new CommandeRepository();
        commandeRepo.saveAll(List.of(premiere, seconde, commandeDepuisPanier));

        CommandeService commandeService = new CommandeService(commandeRepo);
        StatistiqueService statistiques = new StatistiqueService(
                produitRepo, clientRepo, commandeRepo);
        System.out.println("Commandes par statut : " + commandeService.commandesParStatut());
        System.out.println("Total par client : " + commandeService.totalParClient());
        System.out.println("Chiffre d'affaires : " + statistiques.chiffreAffaires());
        System.out.println("Produits vendus distincts : "
                + statistiques.produitsDistinctsCommandes().stream().map(Produit::getNom).toList());
        System.out.println("Page commandes : " + statistiques.commandesPaginees(0, 1));
        System.out.println("Repartition > 500 : " + statistiques.commandesParSeuilMontant());
        System.out.println("Stocks : " + statistiques.stocksParNomProduit());
        System.out.println("Commandes comptees par statut : "
                + statistiques.commandesCompteesParStatut());
        System.out.println("Stock moyen : " + statistiques.stockMoyen());
        System.out.println("Rupture ? " + statistiques.existeProduitEnRupture()
                + " | Prix positifs ? " + statistiques.tousLesPrixPositifs()
                + " | Produit sans nom ? " + statistiques.aucunProduitSansNom());
        System.out.println("Premier produit : "
                + statistiques.produitLeMoinsCher().orElseThrow().getNom());
        System.out.println("Produit le plus cher : "
                + statistiques.produitLePlusCher().orElseThrow().getNom());
        System.out.println("Premiere commande : "
                + statistiques.premiereCommande().orElseThrow());
        System.out.println("Rapport de ventes : " + statistiques.rapportVentes());
        System.out.println("Categories achetees triees : "
                + client.getCategoriesAcheteesTriees());

        statistiques.afficherStocks();
        System.out.println("Commandes EN_ATTENTE : "
                + commandeService.nombreCommandes(StatutCommande.EN_ATTENTE));
    }
}
