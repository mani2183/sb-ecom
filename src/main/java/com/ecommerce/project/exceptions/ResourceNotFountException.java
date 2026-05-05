package com.ecommerce.project.exceptions;

public class ResourceNotFountException extends RuntimeException {
    String resourceName;
    String  field;
    String fieldName;
    Long fieldId;

    public ResourceNotFountException() {
    }

    public ResourceNotFountException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s: %s",resourceName,field,fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFountException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d",resourceName,field,fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
}
