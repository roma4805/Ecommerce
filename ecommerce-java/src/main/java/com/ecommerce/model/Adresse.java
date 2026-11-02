package com.ecommerce.model;

import java.util.Objects;

public class Adresse {
    private Long id;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;

    public Adresse(Long id, String rue, String ville, String codePostal, String pays) {
        this.id = id;
        this.rue = rue;
        this.ville = ville;
        this.codePostal = codePostal;
        this.pays = pays;
    }

    public void modifierAdresse(String rue, String ville, String codePostal, String pays) {
        this.rue = rue;
        this.ville = ville;
        this.codePostal = codePostal;
        this.pays = pays;
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
        if (!(o instanceof Adresse)) return false;
        Adresse a = (Adresse) o;
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
