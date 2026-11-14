package com.ecommerce.model;

public interface Authentifiable {
     /** Methode abstraite : chaque classe fournit son implementation. */
    boolean seConnecter(Identifiants identifiants);

    void seDeconnecter();

    boolean isConnecte();

    /** Methode default : implementation commune, redefinissable. */
    default String statutConnexion() {
        return isConnecte() ? "Connecte" : "Deconnecte";
    }

    /** Methode static : utilitaire appele par Authentifiable.emailValide(...). */
    static boolean emailValide(String email) {
        return email != null && email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }
    
}
