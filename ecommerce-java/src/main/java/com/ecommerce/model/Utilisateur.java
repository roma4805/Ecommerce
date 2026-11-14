package com.ecommerce.model;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Classe ABSTRAITE et SCELLEE (sealed) :
 *  - abstraite : on ne peut pas faire "new Utilisateur(...)", seulement new Client / new Administrateur ;
 *  - sealed ... permits : SEULES Client et Administrateur ont le droit d'en heriter.
 * Elle implemente l'interface Authentifiable et Serializable.
 */
public sealed abstract class Utilisateur implements Authentifiable, Serializable
        permits Client, Administrateur {

    @Serial
    private static final long serialVersionUID = 1L;
    // Encapsulation : tous les attributs sont private, acces via getters/setters
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    /** transient : le mot de passe (hache) n'est JAMAIS ecrit lors de la serialisation. */
    private transient String motDePasse;
    private String telephone;
    /** transient : l'etat de connexion est propre a la session, inutile de le sauvegarder. */
    private transient boolean connecte;

    protected Utilisateur(Long id, String nom, String prenom, String email,
                          String motDePasse, String telephone) {
        this.id = id;
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setMotDePasse(motDePasse);
        setTelephone(telephone);
    }

    /** Methode abstraite : chaque sous-classe DOIT la redefinir. */
    public abstract String getRole();

    // ------------------------------------------------------------------
    // Implementation de l'interface Authentifiable
    // ------------------------------------------------------------------

    @Override
    public boolean seConnecter(Identifiants identifiants) {
        connecte = email.equalsIgnoreCase(identifiants.email())
                && verifierMotDePasse(identifiants.motDePasse());
        return connecte;
    }

    @Override
    public void seDeconnecter() {
        connecte = false;
    }

    @Override
    public boolean isConnecte() {
        return connecte;
    }

    public boolean verifierMotDePasse(String motDePasseSaisi) {
        return motDePasse != null && motDePasse.equals(hacher(motDePasseSaisi));
    }

    /** Le mot de passe est stocke sous forme de hash SHA-256, jamais en clair. */
    private static String hacher(String texte) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(texte.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }

    // ------------------------------------------------------------------
    // Getters / setters avec validation (encapsulation)
    // ------------------------------------------------------------------

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getNomComplet() { return prenom + " " + nom; }

    public void setNom(String nom) {
        this.nom = exigerTexte(nom, "nom");
    }

    public void setPrenom(String prenom) {
        this.prenom = exigerTexte(prenom, "prenom");
    }

    public void setEmail(String email) {
        if (!Authentifiable.emailValide(email)) {
            throw new IllegalArgumentException("Email invalide : " + email);
        }
        this.email = email.trim().toLowerCase();
    }

    public void setMotDePasse(String motDePasse) {
        if (motDePasse == null || motDePasse.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit faire au moins 6 caracteres");
        }
        this.motDePasse = hacher(motDePasse);
    }

    public void setTelephone(String telephone) {
        if (telephone != null && !telephone.matches("^\\+?[0-9 ]{8,15}$")) {
            throw new IllegalArgumentException("Telephone invalide : " + telephone);
        }
        this.telephone = telephone;
    }

    protected static String exigerTexte(String valeur, String champ) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le champ " + champ + " est obligatoire");
        }
        return valeur.trim();
    }

    // ------------------------------------------------------------------
    // Redefinition des methodes de Object
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur u = (Utilisateur) o;
        return Objects.equals(id, u.id) && Objects.equals(email, u.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return getRole() + "{id=" + id + ", nom='" + nom + "', prenom='" + prenom
                + "', email='" + email + "', telephone='" + telephone + "'}";
    }
}
