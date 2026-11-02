package com.ecommerce.model;

import java.math.BigDecimal;

/**
 * Record : equals(), hashCode() et toString() sont generes automatiquement.
 */
public record ProduitResume(String nom, BigDecimal prix) {
}
