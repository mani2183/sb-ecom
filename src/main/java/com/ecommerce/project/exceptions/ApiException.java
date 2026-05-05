package com.ecommerce.project.exceptions;

public class ApiException extends RuntimeException{

    private  static final long serialVersionUID= 1l;

    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }
}
