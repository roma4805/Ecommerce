package com.ecommerce.service;

import com.ecommerce.enums.StatutCommande;
import com.ecommerce.model.Client;
import com.ecommerce.model.Commande;
import com.ecommerce.model.LigneCommande;
import com.ecommerce.repository.CommandeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandeService {
    private final CommandeRepository repository;

    public CommandeService(CommandeRepository repository) {
        this.repository = repository;
    }

    public Commande passerCommande(Client client) {
        Commande commande = client.passerCommande();
        return repository.save(commande);
    }

    public void confirmer(Long idCommande) {
        repository.findById(idCommande)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + idCommande))
                .confirmer();
    }

    public void annuler(Long idCommande) {
        repository.findById(idCommande)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + idCommande))
                .annuler();
    }

    // Intermediaire : map
    public List<BigDecimal> getMontants() {
        return repository.findAll().stream()
                .map(Commande::getMontantTotal)
                .collect(Collectors.toList());
    }

    // Intermediaires : filter, map | Terminale : reduce
    public BigDecimal chiffreAffaires() {
        return repository.findAll().stream()
                .filter(c -> c.getStatut() != StatutCommande.ANNULEE)
                .map(Commande::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Terminale : collect (groupingBy + counting)
    public Map<StatutCommande, Long> compterParStatut() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(Commande::getStatut, Collectors.counting()));
    }

    public EnumMap<StatutCommande, List<Commande>> commandesParStatut() {
        EnumMap<StatutCommande, List<Commande>> resultat = new EnumMap<>(StatutCommande.class);
        for (StatutCommande statut : StatutCommande.values()) {
            resultat.put(statut, new ArrayList<>());
        }
        repository.findAll().forEach(commande ->
                resultat.get(commande.getStatut()).add(commande));
        return resultat;
    }

    public Map<Client, BigDecimal> totalParClient() {
        return repository.findAll().stream()
                .filter(commande -> commande.getStatut() != StatutCommande.ANNULEE)
                .collect(Collectors.toMap(
                        Commande::getClient,
                        Commande::getMontantTotal,
                        BigDecimal::add));
    }

    // Terminale : count
    public long nombreCommandes(StatutCommande statut) {
        return repository.findAll().stream()
                .filter(c -> c.getStatut() == statut)
                .count();
    }

    // Intermediaires : filter, sorted | Terminale : findFirst
    public Optional<Commande> premiereCommandeDuClient(Client client) {
        return repository.findAll().stream()
                .filter(c -> c.getClient().equals(client))
                .sorted(Comparator.comparing(Commande::getDateCommande))
                .findFirst();
    }

    // Intermediaire : flatMap
    public List<LigneCommande> toutesLesLignes() {
        return repository.findAll().stream()
                .flatMap(c -> c.getLignes().stream())
                .collect(Collectors.toList());
    }

    public List<Commande> toutesLesCommandes() {
        return repository.findAll();
    }

    public Optional<Commande> trouver(Long id) {
        return repository.findById(id);
    }
}
