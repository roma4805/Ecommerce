package com.ecommerce.exception;

import java.io.Serial;

public class ProduitIntrouvableException extends EcommerceException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Long idProduit;

    public ProduitIntrouvableException(Long idProduit) {
        super("Aucun produit avec l'id " + idProduit);
        this.idProduit = idProduit;
    }

    public Long getIdProduit() {
        return idProduit;
    }
    
}
