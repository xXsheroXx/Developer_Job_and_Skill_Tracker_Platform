package com.shero.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// exception/ResourceNotFoundException.java
@ResponseStatus(HttpStatus.NOT_FOUND)  // automatically returns 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
