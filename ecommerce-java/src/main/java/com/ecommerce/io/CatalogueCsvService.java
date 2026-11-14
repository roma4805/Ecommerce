package com.ecommerce.io;

import com.ecommerce.model.Categorie;
import com.ecommerce.model.Produit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Export / import du catalogue au format CSV (separateur ";").
 * FLUX DE CARACTERES : BufferedWriter / BufferedReader, crees via l'API NIO.2 (Files).
 */
public class CatalogueCsvService {
    public static final String ENTETE = "id;nom;description;prix;stock;actif;categorie";
    private static final String SEP = ";";

    /** Ecrit le catalogue. try-with-resources : le fichier est toujours ferme. */
    public void exporter(Collection<Produit> produits, Path fichier) throws IOException {
        if (fichier.getParent() != null) {
            Files.createDirectories(fichier.getParent());          // NIO.2
        }
        try (BufferedWriter writer = Files.newBufferedWriter(fichier, StandardCharsets.UTF_8)) {
            writer.write(ENTETE);
            writer.newLine();
            for (Produit p : produits) {
                writer.write(String.join(SEP,
                        String.valueOf(p.getId()),
                        nettoyer(p.getNom()),
                        nettoyer(p.getDescription()),
                        p.getPrix().toPlainString(),
                        String.valueOf(p.getStock()),
                        String.valueOf(p.isActif()),
                        p.getCategorie() == null ? "" : nettoyer(p.getCategorie().getNom())));
                writer.newLine();
            }
        }
    }

    /**
     * Relit le catalogue. Les categories sont recreees a partir de leur nom.
     * Une ligne mal formee est ignoree (et signalee) au lieu de tout faire echouer.
     */
    public List<Produit> importer(Path fichier) throws IOException {
        if (!Files.exists(fichier)) {
            throw new IOException("Fichier introuvable : " + fichier.toAbsolutePath());
        }
        List<Produit> produits = new ArrayList<>();
        Map<String, Categorie> categories = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            String ligne = reader.readLine();                       // on saute l'entete
            int numero = 1;
            while ((ligne = reader.readLine()) != null) {
                numero++;
                if (ligne.isBlank()) continue;
                try {
                    produits.add(lireLigne(ligne, categories));
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("  Ligne " + numero + " ignoree : " + e.getMessage());
                }
            }
        }
        return produits;
    }

    private Produit lireLigne(String ligne, Map<String, Categorie> categories) {
        String[] c = ligne.split(SEP, -1);
        if (c.length != 7) {
            throw new IllegalArgumentException("7 colonnes attendues, " + c.length + " trouvees");
        }
        Categorie categorie = c[6].isBlank() ? null
                : categories.computeIfAbsent(c[6],
                        nom -> new Categorie((long) categories.size() + 1, nom, ""));
        Produit p = new Produit(Long.parseLong(c[0]), c[1], c[2],
                new BigDecimal(c[3]), Integer.parseInt(c[4]), categorie);
        p.setActif(Boolean.parseBoolean(c[5]));
        return p;
    }

    /** Le separateur ne doit pas apparaitre dans une valeur. */
    private static String nettoyer(String valeur) {
        return valeur == null ? "" : valeur.replace(SEP, ",").replace("\n", " ");
    }
}
