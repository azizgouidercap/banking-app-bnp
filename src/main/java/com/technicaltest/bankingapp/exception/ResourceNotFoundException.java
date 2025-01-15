package com.technicaltest.bankingapp.exception;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with ID " + id + " not found.");
    }
}
