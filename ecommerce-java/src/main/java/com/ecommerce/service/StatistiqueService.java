package com.ecommerce.service;

import com.ecommerce.enums.StatutCommande;
import com.ecommerce.model.Adresse;
import com.ecommerce.model.Avis;
import com.ecommerce.model.Commande;
import com.ecommerce.model.LigneCommande;
import com.ecommerce.model.Produit;
import com.ecommerce.repository.ClientRepository;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.ProduitRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatistiqueService {
    private final ProduitRepository produits;
    private final ClientRepository clients;
    private final CommandeRepository commandes;

    public StatistiqueService(ProduitRepository produits, ClientRepository clients,
                              CommandeRepository commandes) {
        this.produits = produits;
        this.clients = clients;
        this.commandes = commandes;
    }

    // Intermediaires : filter, flatMap, sorted, limit, map | Terminale : collect
    public List<String> topProduitsVendus(int n) {
        Map<String, Integer> quantites = commandes.findAll().stream()
                .filter(c -> c.getStatut() != StatutCommande.ANNULEE)
                .flatMap(c -> c.getLignes().stream())
                .collect(Collectors.groupingBy(LigneCommande::getNomProduit,
                        Collectors.summingInt(LigneCommande::getQuantite)));
        return quantites.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .map(e -> e.getKey() + " (" + e.getValue() + " vendus)")
                .collect(Collectors.toList());
    }

    public List<Produit> produitsDistinctsCommandes() {
        return commandes.findAll().stream()
                .filter(c -> c.getStatut() != StatutCommande.ANNULEE)
                .flatMap(c -> c.getLignes().stream())
                .map(LigneCommande::getProduit)
                .distinct()
                .toList();
    }

    public List<Commande> commandesPaginees(int page, int taille) {
        if (page < 0 || taille <= 0) {
            throw new IllegalArgumentException("Page ou taille invalide");
        }
        return commandes.findAll().stream()
                .skip((long) page * taille)
                .limit(taille)
                .toList();
    }

    public BigDecimal chiffreAffaires() {
        return commandes.findAll().stream()
                .filter(c -> c.getStatut() != StatutCommande.ANNULEE)
                .map(Commande::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Boolean, List<Commande>> commandesParSeuilMontant() {
        return commandes.findAll().stream()
                .collect(Collectors.partitioningBy(
                        commande -> commande.getMontantTotal().compareTo(new BigDecimal("500")) > 0));
    }

    public Map<String, Integer> stocksParNomProduit() {
        return produits.findAll().stream()
                .collect(Collectors.toMap(Produit::getNom, Produit::getStock));
    }

    public Map<StatutCommande, Long> commandesCompteesParStatut() {
        return commandes.findAll().stream()
                .collect(Collectors.groupingBy(Commande::getStatut, Collectors.counting()));
    }

    public double stockMoyen() {
        return produits.findAll().stream().collect(Collectors.averagingInt(Produit::getStock));
    }

    // Intermediaires : flatMap, map, distinct, sorted
    public List<String> villesDesClients() {
        return clients.findAll().stream()
                .flatMap(c -> c.getAdresses().stream())
                .map(Adresse::getVille)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Terminale : average (via mapToInt)
    public double noteMoyenneGlobale() {
        return produits.findAll().stream()
                .flatMap(p -> p.getAvis().stream())
                .collect(Collectors.averagingInt(Avis::getNote));
    }

    // Terminale : sum (via mapToInt)
    public int stockTotal() {
        return produits.findAll().stream().mapToInt(Produit::getStock).sum();
    }

    // Terminale : allMatch
    public boolean tousLesPrixPositifs() {
        return produits.findAll().stream().allMatch(p -> p.getPrix().signum() > 0);
    }

    public boolean existeProduitEnRupture() {
        return produits.findAll().stream().anyMatch(p -> p.getStock() == 0);
    }

    // Terminale : noneMatch
    public boolean aucunProduitSansNom() {
        return produits.findAll().stream().noneMatch(p -> p.getNom() == null || p.getNom().isBlank());
    }

    // Terminale : min
    public Optional<Produit> produitLeMoinsCher() {
        return produits.findAll().stream().min(Comparator.comparing(Produit::getPrix));
    }

    public Optional<Produit> produitLePlusCher() {
        return produits.findAll().stream().max(Comparator.comparing(Produit::getPrix));
    }

    public Optional<Commande> premiereCommande() {
        return commandes.findAll().stream().findFirst();
    }

    public List<String> rapportVentes() {
        class LigneRapport {
            private final String produit;
            private final int quantite;
            private final BigDecimal chiffreAffaires;

            private LigneRapport(String produit, int quantite, BigDecimal chiffreAffaires) {
                this.produit = produit;
                this.quantite = quantite;
                this.chiffreAffaires = chiffreAffaires;
            }

            @Override
            public String toString() {
                return produit + " | quantite=" + quantite + " | chiffre d'affaires="
                        + chiffreAffaires;
            }
        }

        Map<String, Integer> quantites = new HashMap<>();
        Map<String, BigDecimal> montants = new HashMap<>();
        commandes.findAll().stream()
                .filter(commande -> commande.getStatut() != StatutCommande.ANNULEE)
                .flatMap(commande -> commande.getLignes().stream())
                .forEach(ligne -> {
                    quantites.merge(ligne.getNomProduit(), ligne.getQuantite(), Integer::sum);
                    montants.merge(ligne.getNomProduit(), ligne.calculerSousTotal(), BigDecimal::add);
                });
        return quantites.keySet().stream()
                .sorted()
                .map(nom -> new LigneRapport(nom, quantites.get(nom), montants.get(nom)).toString())
                .toList();
    }

    // Intermediaire : peek | Terminale : forEach
    public void afficherStocks() {
        produits.findAll().stream()
                .peek(p -> System.out.print("  [stock] "))
                .map(p -> p.getNom() + " -> " + p.getStock())
                .forEach(System.out::println);
    }

    // Intermediaire : skip | Terminale : count
    public long nombreCommandesApresLaPremiere() {
        return commandes.findAll().stream().skip(1).count();
    }
}
