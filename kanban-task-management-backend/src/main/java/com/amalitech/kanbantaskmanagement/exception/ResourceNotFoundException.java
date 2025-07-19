package com.amalitech.kanbantaskmanagement.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String field, String value) {
        super(resource + " with " + field + " = '" + value + "' not found.");
    }
}
