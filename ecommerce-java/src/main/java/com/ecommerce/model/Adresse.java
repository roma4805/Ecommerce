package com.ecommerce.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Adresse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;

    public Adresse(Long id, String rue, String ville, String codePostal, String pays) {
        this.id = id;
        modifierAdresse(rue, ville, codePostal, pays);
    }

    /** Validation centralisee : une adresse n'est jamais dans un etat incoherent. */
    public void modifierAdresse(String rue, String ville, String codePostal, String pays) {
        if (codePostal == null || !codePostal.matches("\\d{5}")) {
            throw new IllegalArgumentException("Code postal invalide (5 chiffres) : " + codePostal);
        }
        this.rue = exiger(rue, "rue");
        this.ville = exiger(ville, "ville");
        this.codePostal = codePostal;
        this.pays = exiger(pays, "pays");
    }

    private static String exiger(String valeur, String champ) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le champ " + champ + " est obligatoire");
        }
        return valeur.trim();
    }

    public String afficherAdresse() {
        return rue + ", " + codePostal + " " + ville + ", " + pays;
    }

    public Long getId() { return id; }
    public String getRue() { return rue; }
    public String getVille() { return ville; }
    public String getCodePostal() { return codePostal; }
    public String getPays() { return pays; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Adresse a)) return false;
        return Objects.equals(id, a.id) && Objects.equals(rue, a.rue)
                && Objects.equals(ville, a.ville) && Objects.equals(codePostal, a.codePostal)
                && Objects.equals(pays, a.pays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rue, ville, codePostal, pays);
    }

    @Override
    public String toString() {
        return "Adresse{id=" + id + ", " + afficherAdresse() + "}";
    }
}
