package com.ecommerce.exception;

import java.io.Serial;

public class EcommerceException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;
    
    public EcommerceException(String message){
        super(message);
    }

    public  EcommerceException(String message,Throwable cause){
        super(message,cause);
    }
    
}