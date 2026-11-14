package com.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.ecommerce.exception.AuthentificationException;
import com.ecommerce.model.Administrateur;
import com.ecommerce.model.Client;
import com.ecommerce.model.Identifiants;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.ClientRepository;

public class AuthService {
    private final ClientRepository clients;
    private final List<Administrateur> administrateurs = new ArrayList<>();

    public AuthService(ClientRepository clients) {
        this.clients = clients;
    }

    public void ajouterAdministrateur(Administrateur admin) {
        administrateurs.add(admin);
    }

    /**
     * Renvoie un Utilisateur : le type reel est Client ou Administrateur (up-casting implicite).
     * Exception CHECKED declaree avec throws.
     */
    public Utilisateur connecter(Identifiants identifiants) throws AuthentificationException {
        Utilisateur utilisateur = trouverParEmail(identifiants.email())
                .orElseThrow(() -> new AuthentificationException(
                        "Aucun compte pour " + identifiants.email()));

        if (!utilisateur.seConnecter(identifiants)) {
            throw new AuthentificationException("Mot de passe incorrect");
        }
        return utilisateur;
    }

    /** Version qui ne propage pas l'exception : try-catch-finally. */
    public Optional<Utilisateur> tenterConnexion(String email, String motDePasse) {
        try {
            return Optional.of(connecter(new Identifiants(email, motDePasse)));
        } catch (AuthentificationException | IllegalArgumentException e) {   // multi-catch
            System.out.println("  Echec de connexion : " + e.getMessage());
            return Optional.empty();
        } finally {
            System.out.println("  Tentative de connexion pour " + email + " terminee");
        }
    }

    private Optional<Utilisateur> trouverParEmail(String email) {
        // Up-casting : un Stream<Client> et un Stream<Administrateur> deviennent Stream<Utilisateur>
        Stream<Utilisateur> tous = Stream.concat(
                clients.findAll().stream().map(c -> (Utilisateur) c),
                administrateurs.stream().map(a -> (Utilisateur) a));
        return tous.filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    /**
     * Down-casting par pattern matching : on teste le type reel et on recupere
     * directement une variable du bon type (c ou a), sans cast manuel.
     */
    public String menuPour(Utilisateur u) {
        if (u instanceof Client c) {
            return "Menu CLIENT de " + c.getNomComplet()
                    + " | panier : " + c.getPanier().getLignes().size() + " article(s)"
                    + " | adresses : " + c.getAdresses().size();
        } else if (u instanceof Administrateur a) {
            return "Menu ADMIN de " + a.getNomComplet() + " | niveau : " + a.getNiveauAcces();
        }
        // Inatteignable grace a "sealed" : Utilisateur n'a que ces deux sous-classes.
        throw new IllegalStateException("Type d'utilisateur inconnu");
    }

    /** Ancien style de down-casting (avant le pattern matching), pour comparer. */
    public Client exigerClient(Utilisateur u) {
        if (!(u instanceof Client)) {
            throw new ClassCastException(u.getRole() + " n'est pas un client");
        }
        return (Client) u;   // cast explicite
    }
    
}
