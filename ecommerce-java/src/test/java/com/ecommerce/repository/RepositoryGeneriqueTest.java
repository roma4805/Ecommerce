package com.ecommerce.repository;

import com.ecommerce.model.Produit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RepositoryGeneriqueTest {
    @Test
    void pecsEtPaginationFonctionnentSurLeRepositoryMemoire() {
        ProduitRepository repository = new ProduitRepository();
        Produit clavier = new Produit(1L, "Clavier", "", new BigDecimal("10"), 3, null);
        Produit souris = new Produit(2L, "Souris", "", new BigDecimal("20"), 5, null);
        repository.saveAll(List.of(clavier, souris));

        Predicate<Object> objets = objet -> objet.toString().contains("Clavier");
        assertEquals(List.of(clavier), repository.findBy(objets));
        List<Object> destination = new ArrayList<>();
        repository.copierVers(destination);
        assertEquals(2, destination.size());
        assertEquals(List.of(souris), repository.findPage(1, 1).elements());
        assertThrows(IllegalArgumentException.class, () -> repository.findPage(-1, 1));
    }
}
