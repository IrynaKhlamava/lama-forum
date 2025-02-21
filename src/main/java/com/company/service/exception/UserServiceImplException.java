package com.company.service.exception;

import com.company.service.impl.UserServiceImpl;

public class UserServiceImplException extends RuntimeException{

    public UserServiceImplException(String message) {
        super(message);
    }
}
