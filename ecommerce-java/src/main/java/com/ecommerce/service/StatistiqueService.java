package com.ecommerce.service;

import com.ecommerce.enums.StatutCommande;
import com.ecommerce.model.Adresse;
import com.ecommerce.model.Avis;
import com.ecommerce.model.LigneCommande;
import com.ecommerce.model.Produit;
import com.ecommerce.repository.ClientRepository;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.ProduitRepository;

import java.util.Comparator;
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
                .mapToInt(Avis::getNote)
                .average()
                .orElse(0.0);
    }

    // Terminale : sum (via mapToInt)
    public int stockTotal() {
        return produits.findAll().stream().mapToInt(Produit::getStock).sum();
    }

    // Terminale : allMatch
    public boolean tousLesPrixPositifs() {
        return produits.findAll().stream().allMatch(p -> p.getPrix().signum() > 0);
    }

    // Terminale : noneMatch
    public boolean aucunProduitSansNom() {
        return produits.findAll().stream().noneMatch(p -> p.getNom() == null || p.getNom().isBlank());
    }

    // Terminale : min
    public Optional<Produit> produitLeMoinsCher() {
        return produits.findAll().stream().min(Comparator.comparing(Produit::getPrix));
    }

    // Intermediaire : peek | Terminale : forEach
    public void afficherStocks() {
        produits.findAll().stream()
                .peek(p -> System.out.print("  [stock] "))
                .forEach(p -> System.out.println(p.getNom() + " -> " + p.getStock()));
    }

    // Intermediaire : skip | Terminale : count
    public long nombreCommandesApresLaPremiere() {
        return commandes.findAll().stream().skip(1).count();
    }
}
