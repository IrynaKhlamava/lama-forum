package com.company.controller;

import com.company.service.exception.AdminInvitationImplServiceException;
import com.company.service.exception.EmailSendingException;
import com.company.service.exception.UserRegistrationException;
import com.company.service.exception.UserServiceImplException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

   private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AdminInvitationImplServiceException.class)
    public ModelAndView handleInvalidInvitation(AdminInvitationImplServiceException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(UserServiceImplException.class)
    public ModelAndView handleUserServiceImplException(UserServiceImplException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(EmailSendingException.class)
    public ModelAndView handleEmailException(EmailSendingException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "Error sending email: " + ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleNotFoundException(EntityNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ModelAndView handleUserRegistrationException(UserRegistrationException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        logger.error("An error has occurred: ", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "Something went wrong. Please try again later");
        return modelAndView;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFound(NoHandlerFoundException ex) {
        logger.error("Requested page not found: {}", ex.getRequestURL());
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "The page you requested does not exist.");
        return modelAndView;
    }

}
