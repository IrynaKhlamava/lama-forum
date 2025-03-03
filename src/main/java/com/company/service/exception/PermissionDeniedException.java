package com.company.service.exception;

public class PermissionDeniedException extends RuntimeException{

    public PermissionDeniedException(String message) {

        super(message);
    }

}
