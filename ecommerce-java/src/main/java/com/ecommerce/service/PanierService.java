package com.ecommerce.service;

import com.ecommerce.model.Client;
import com.ecommerce.model.LignePanier;
import com.ecommerce.model.Produit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class PanierService {

    public void ajouter(Client client, Produit produit, int quantite) {
        exigerClient(client).ajouterAuPanier(
                Objects.requireNonNull(produit, "Produit obligatoire"), quantite);
    }

    public void retirer(Client client, Produit produit) {
        exigerClient(client).getPanier().retirerProduit(
                Objects.requireNonNull(produit, "Produit obligatoire"));
    }

    public void changerQuantite(Client client, Produit produit, int quantite) {
        exigerClient(client).modifierQuantitePanier(
                Objects.requireNonNull(produit, "Produit obligatoire"), quantite);
    }

    public void vider(Client client) {
        exigerClient(client).getPanier().vider();
    }

    public List<LignePanier> consulter(Client client) {
        return List.copyOf(exigerClient(client).getPanier().getLignes());
    }

    public BigDecimal total(Client client) {
        return exigerClient(client).getPanier().calculerTotal();
    }

    public int nombreArticles(Client client) {
        return exigerClient(client).getPanier().getNombreArticles();
    }

    public boolean estVide(Client client) {
        return exigerClient(client).getPanier().estVide();
    }

    private static Client exigerClient(Client client) {
        return Objects.requireNonNull(client, "Client obligatoire");
    }

}
