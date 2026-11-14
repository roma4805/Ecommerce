package com.ecommerce.exception;

import java.io.Serial;

public class AuthentificationException extends EcommerceException {
    @Serial
    private static final long serialVersionUID = 1L;
    public AuthentificationException(String message){
        super(message);
    }
    
}
