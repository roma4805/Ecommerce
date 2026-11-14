package com.ecommerce.service;

import com.ecommerce.exception.ProduitIntrouvableException;
import com.ecommerce.model.Categorie;
import com.ecommerce.model.FiltreProduit;
import com.ecommerce.model.Produit;
import com.ecommerce.model.ProduitResume;
import com.ecommerce.repository.ProduitRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProduitService {
    private final ProduitRepository repository;

    public ProduitService(ProduitRepository repository) {
        this.repository = repository;
    }

    public Produit ajouter(Produit p) {
        return repository.save(p);
    }

    // Intermediaire : filter | Terminale : collect
    public List<Produit> getProduitsDisponibles() {
        return repository.findAll().stream()
                .filter(p -> p.isActif() && p.getStock() > 0)
                .collect(Collectors.toList());
    }

    // Intermediaire : map | Terminale : collect
    public List<String> getNomsProduits() {
        return repository.findAll().stream()
                .map(Produit::getNom)
                .collect(Collectors.toList());
    }

    // Intermediaire : sorted | Terminale : toList
    public List<Produit> getProduitsTriesParPrix() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Produit::getPrix))
                .toList();
    }

    // Terminale : collect (groupingBy)
    public Map<String, List<Produit>> getProduitsParCategorie() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategorie() == null ? "Sans categorie" : p.getCategorie().getNom()));
    }

    // Intermediaires : sorted, skip, limit
    public List<Produit> getPage(int numeroPage, int taille) {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Produit::getNom))
                .skip((long) numeroPage * taille)
                .limit(taille)
                .collect(Collectors.toList());
    }

    /** Exception CHECKED : l'appelant est oblige de gerer le cas "produit introuvable". */
    public Produit trouverParId(Long id) throws ProduitIntrouvableException {
        return repository.findById(id)
                .orElseThrow(() -> new ProduitIntrouvableException(id));
    }

    // ---- SURCHARGE de rechercher(...) : meme nom, parametres differents ----

    // Lambda : Predicate | Intermediaire : filter
    public List<Produit> rechercher(String motCle) {
        Predicate<Produit> contientMotCle =
                p -> p.getNom().toLowerCase().contains(motCle.toLowerCase());
        return repository.findBy(contientMotCle);
    }

    public List<Produit> rechercher(Categorie categorie) {
        return rechercher(FiltreProduit.parCategorie(categorie));
    }

    public List<Produit> rechercher(BigDecimal prixMin, BigDecimal prixMax) {
        FiltreProduit auDessusDuMin = p -> p.getPrix().compareTo(prixMin) >= 0;
        return rechercher(auDessusDuMin.et(FiltreProduit.prixMax(prixMax)));
    }

    /** Interface fonctionnelle maison : on l'adapte en Predicate par reference de methode. */
    public List<Produit> rechercher(FiltreProduit filtre) {
        return repository.findBy(filtre::accepter);
    }

    // Terminale : anyMatch
    public boolean existeRupture() {
        return repository.findAll().stream().anyMatch(p -> p.getStock() == 0);
    }

    // Terminale : max
    public Optional<Produit> produitLePlusCher() {
        return repository.findAll().stream().max(Comparator.comparing(Produit::getPrix));
    }

    // Terminale : findFirst
    public Optional<Produit> premierProduitSousPrix(BigDecimal prixMax) {
        return repository.findAll().stream()
                .filter(p -> p.getPrix().compareTo(prixMax) < 0)
                .findFirst();
    }

    // Record + Intermediaire : map
    public List<ProduitResume> getResumes() {
        return repository.findAll().stream()
                .map(p -> new ProduitResume(p.getNom(), p.getPrix()))
                .collect(Collectors.toList());
    }
}
