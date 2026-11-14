package com.ecommerce.model;

public record Identifiants(String email,String motDePasse) {
     public Identifiants {
        if (!Authentifiable.emailValide(email)) {
            throw new IllegalArgumentException("Email invalide : " + email);
        }
        if (motDePasse == null || motDePasse.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        email = email.trim().toLowerCase();
    }

    /** On redefinit toString() pour ne jamais afficher le mot de passe. */
    @Override
    public String toString() {
        return "Identifiants[email=" + email + ", motDePasse=****]";
    }
}