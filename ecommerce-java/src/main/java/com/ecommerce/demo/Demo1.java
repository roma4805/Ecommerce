package com.ecommerce.demo;

import com.ecommerce.exception.AuthentificationException;
import com.ecommerce.exception.ProduitIntrouvableException;
import com.ecommerce.exception.StockInsuffisantException;
import com.ecommerce.io.CatalogueCsvService;
import com.ecommerce.io.ClientSerializer;
import com.ecommerce.io.FichierUtils;
import com.ecommerce.model.Administrateur;
import com.ecommerce.model.Adresse;
import com.ecommerce.model.Authentifiable;
import com.ecommerce.model.Categorie;
import com.ecommerce.model.Client;
import com.ecommerce.model.FiltreProduit;
import com.ecommerce.model.Identifiants;
import com.ecommerce.model.Produit;
import com.ecommerce.model.ProduitResume;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.ClientRepository;
import com.ecommerce.repository.ProduitRepository;
import com.ecommerce.service.AuthService;
import com.ecommerce.service.ProduitService;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Demonstration de tous les concepts de Dev 1, dans l'ordre de la grille.
 * Lancer : mvn compile exec:java -Dexec.mainClass=com.ecommerce.demo.DemoDev1
 *     ou : clic droit > Run dans l'IDE.
 */
public class Demo1 {

    public static void main(String[] args) throws Exception {
        // ---------- Jeu de donnees ----------
        ProduitRepository produitRepo = new ProduitRepository();
        ClientRepository clientRepo = new ClientRepository();
        ProduitService produitService = new ProduitService(produitRepo);
        AuthService authService = new AuthService(clientRepo);

        Categorie info = new Categorie(1L, "Informatique", "PC et accessoires");
        Categorie livres = new Categorie(2L, "Livres", "Romans et BD");

        Administrateur admin = new Administrateur(100L, "Alaoui", "Sara",
                "admin@shop.ma", "admin123", "0600000000");
        authService.ajouterAdministrateur(admin);

        // Surcharge : deux facons d'ajouter un produit
        admin.ajouterProduit(new Produit(1L, "Clavier mecanique", "Switch rouges",
                new BigDecimal("450.00"), 10, info), produitRepo);
        admin.ajouterProduit("Souris sans fil", "2.4 GHz", new BigDecimal("150.00"), 25, info, produitRepo);
        admin.ajouterProduit("Ecran 27 pouces", "IPS 144 Hz", new BigDecimal("2300.00"), 3, info, produitRepo);
        admin.ajouterProduit("Le Petit Prince", "Poche", new BigDecimal("45.00"), 1, livres, produitRepo);

        Client client = new Client(1L, "Benali", "Youssef", "youssef@mail.ma", "secret99", "0611223344");
        client.ajouterAdresse(new Adresse(1L, "12 rue Ibn Sina", "Rabat", "10000", "Maroc"));
        clientRepo.save(client);

        titre("1. Classes, objets, encapsulation");
        System.out.println(client);
        try {
            client.setEmail("pas-un-email");
        } catch (IllegalArgumentException e) {
            System.out.println("Setter refuse : " + e.getMessage());
        }
        try {
            new Produit(9L, "Produit faux", "", new BigDecimal("-5"), 1, null);
        } catch (IllegalArgumentException e) {
            System.out.println("Constructeur refuse : " + e.getMessage());
        }

        titre("2. Records");
        Identifiants id = new Identifiants("Youssef@Mail.ma", "secret99");
        System.out.println(id + " -> email normalise : " + id.email());
        System.out.println("equals genere : " + id.equals(new Identifiants("youssef@mail.ma", "secret99")));
        produitService.getResumes().stream().limit(2).forEach(System.out::println);
        System.out.println(new ProduitResume("Test", BigDecimal.ONE));

        titre("3. Sealed classes et classe abstraite");
        System.out.println("Utilisateur est sealed ? " + Utilisateur.class.isSealed());
        for (Class<?> c : Utilisateur.class.getPermittedSubclasses()) {
            System.out.println("  sous-classe autorisee : " + c.getSimpleName());
        }
        System.out.println("getRole() abstrait, redefini : " + client.getRole() + " / " + admin.getRole());

        titre("4. Interfaces et interface fonctionnelle");
        System.out.println("Methode static : emailValide('a@b.ma') = " + Authentifiable.emailValide("a@b.ma"));
        System.out.println("Methode default : " + client.statutConnexion());
        FiltreProduit pasCher = p -> p.getPrix().compareTo(new BigDecimal("500")) < 0;   // lambda
        FiltreProduit infoPasCher = pasCher.et(FiltreProduit.parCategorie(info));
        System.out.println("Informatique < 500 DH : " + noms(produitService.rechercher(infoPasCher)));
        System.out.println("Inverse : " + noms(produitService.rechercher(infoPasCher.inverse())));

        titre("5. Redefinition vs surcharge");
        System.out.println("Redefinition toString() : " + admin);
        System.out.println("Surcharge rechercher(String)          : " + noms(produitService.rechercher("souris")));
        System.out.println("Surcharge rechercher(Categorie)       : " + noms(produitService.rechercher(livres)));
        System.out.println("Surcharge rechercher(BigDecimal, ...) : "
                + noms(produitService.rechercher(new BigDecimal("100"), new BigDecimal("500"))));
        client.laisserAvis(produitRepo.findById(1L).orElseThrow(), 5);
        client.laisserAvis(produitRepo.findById(1L).orElseThrow(), 4, "Tres bon clavier");
        System.out.println("Surcharge laisserAvis : " + client.getAvisRediges().size() + " avis");

        titre("6. Up-casting et down-casting (pattern matching)");
        Utilisateur u1 = authService.connecter(new Identifiants("youssef@mail.ma", "secret99"));
        Utilisateur u2 = authService.connecter(new Identifiants("admin@shop.ma", "admin123"));
        System.out.println("Type declare Utilisateur, type reel : " + u1.getClass().getSimpleName()
                + " et " + u2.getClass().getSimpleName());
        System.out.println(authService.menuPour(u1));
        System.out.println(authService.menuPour(u2));
        try {
            authService.exigerClient(u2);
        } catch (ClassCastException e) {
            System.out.println("Down-casting refuse : " + e.getMessage());
        }

        titre("7. Exceptions (throw, throws, try-catch)");
        try {
            produitService.trouverParId(999L);                   // checked
        } catch (ProduitIntrouvableException e) {
            System.out.println("Checked attrapee : " + e.getMessage());
        }
        try {
            authService.connecter(new Identifiants("youssef@mail.ma", "mauvais"));
        } catch (AuthentificationException e) {
            System.out.println("Checked attrapee : " + e.getMessage());
        }
        try {
            client.ajouterAuPanier(produitRepo.findById(4L).orElseThrow(), 5);   // unchecked
        } catch (StockInsuffisantException e) {
            System.out.println("Unchecked attrapee : " + e.getMessage());
        }
        authService.tenterConnexion("inconnu@mail.ma", "xxxxxx");   // multi-catch + finally

        titre("8. Flux de caracteres + NIO.2 : export / import CSV");
        Path dossier = FichierUtils.preparerDossier("data");
        Path csv = dossier.resolve("catalogue.csv");
        CatalogueCsvService csvService = new CatalogueCsvService();
        csvService.exporter(produitRepo.findAll(), csv);
        System.out.println("Exporte : " + FichierUtils.description(csv));
        Files.readAllLines(csv).forEach(l -> System.out.println("  | " + l));
        Files.writeString(csv, "ligne;cassee\n", java.nio.file.StandardOpenOption.APPEND);
        List<Produit> relus = csvService.importer(csv);
        System.out.println("Relus : " + relus.size() + " produits -> " + noms(relus));

        titre("9. Flux d'octets : copie d'un fichier binaire");
        Path image = dossier.resolve("clavier.bin");
        Files.write(image, new byte[]{(byte) 0x89, 'P', 'N', 'G', 1, 2, 3, 4, 5});
        long octets = FichierUtils.copierOctets(image, dossier.resolve("images").resolve("clavier-copie.bin"));
        System.out.println("Octets copies : " + octets);
        System.out.println("Fichiers .csv du dossier data : " + FichierUtils.listerFichiers(dossier, ".csv"));

        titre("10. Serialisation et transient");
        client.ajouterAuPanier(produitRepo.findById(2L).orElseThrow(), 2);
        client.seConnecter(new Identifiants("youssef@mail.ma", "secret99"));
        Path ser = dossier.resolve("clients.ser");
        ClientSerializer serializer = new ClientSerializer();
        serializer.sauvegarder(clientRepo.findAll(), ser);
        System.out.println("Avant : connecte=" + client.isConnecte()
                + ", panier=" + client.getPanier().getLignes().size() + " ligne(s)"
                + ", mdp ok=" + client.verifierMotDePasse("secret99"));
        Client recharge = serializer.charger(ser).get(0);
        System.out.println("Apres : " + recharge);
        System.out.println("Apres : connecte=" + recharge.isConnecte()
                + ", panier=" + recharge.getPanier().getLignes().size() + " ligne(s)"
                + ", mdp ok=" + recharge.verifierMotDePasse("secret99")
                + "  <- champs transient non sauvegardes");
        System.out.println("Champs normaux conserves : " + recharge.getAdresses().get(0).afficherAdresse()
                + ", " + recharge.getAvisRediges().size() + " avis");
    }

    private static void titre(String t) {
        System.out.println();
        System.out.println("=== " + t + " ===");
    }

    private static List<String> noms(List<Produit> produits) {
        return produits.stream().map(Produit::getNom).toList();
    }
}
