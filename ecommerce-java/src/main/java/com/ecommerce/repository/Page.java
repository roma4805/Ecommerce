
package com.ecommerce.repository;

import java.util.List;

public record Page<T>(List<T> elements, int numero, int taille) {

    public Page {
        if (elements == null) {
            throw new IllegalArgumentException("La liste ne peut pas être null");
        }

        if (numero < 0 || taille <= 0) {
            throw new IllegalArgumentException("Numero ou taille invalide");
        }

        elements = List.copyOf(elements);
    }

    public int nombreElements() {
        return elements.size();
    }

    public boolean estVide() {
        return elements.isEmpty();
    }
}
