package com.ecommerce.exception;

import java.io.Serial;

public class StockInsuffisantException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    private final String nomProduit;
    private final int quantiteDemandee;
    private final int stockDisponible;

    public StockInsuffisantException(String nomProduit, int quantiteDemandee, int stockDisponible) {
        super("Stock insuffisant pour " + nomProduit + " : demande " + quantiteDemandee
                + ", disponible " + stockDisponible);
        this.nomProduit = nomProduit;
        this.quantiteDemandee = quantiteDemandee;
        this.stockDisponible = stockDisponible;
    }

    public String getNomProduit() { return nomProduit; }
    public int getQuantiteDemandee() { return quantiteDemandee; }
    public int getStockDisponible() { return stockDisponible; }
}
