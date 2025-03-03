package com.company.controller;

import com.company.service.exception.AdminInvitationImplServiceException;
import com.company.service.exception.EmailSendingException;
import com.company.service.exception.PermissionDeniedException;
import com.company.service.exception.ResourceNotFoundException;
import com.company.service.exception.UserRegistrationException;
import com.company.service.exception.UserServiceImplException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

   private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            UserRegistrationException.class,
            AdminInvitationImplServiceException.class,
            UserServiceImplException.class,
            EmailSendingException.class,
            ResourceNotFoundException.class,
            PermissionDeniedException.class
    })

    public ModelAndView handleExceptions(Exception ex) {
        logger.error("Exception occurred in {}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", ex.getMessage());
        return modelAndView;
    }

}
