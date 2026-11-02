package com.ecommerce.model;

import java.util.Objects;

public abstract class Utilisateur {
    protected Long id;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String motDePasse;
    protected String telephone;
    private boolean connecte;

    public Utilisateur(Long id, String nom, String prenom, String email,
                       String motDePasse, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
    }

    public boolean seConnecter() {
        connecte = true;
        return connecte;
    }

    public void seDeconnecter() {
        connecte = false;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public boolean isConnecte() { return connecte; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

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
        return "Utilisateur{id=" + id + ", nom='" + nom + "', prenom='" + prenom
                + "', email='" + email + "', telephone='" + telephone + "'}";
    }
}
