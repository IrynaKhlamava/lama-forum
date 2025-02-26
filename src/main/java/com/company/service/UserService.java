package com.company.service;

import com.company.model.User;

public interface UserService{

    String registerUser(User user);

    boolean activateUser(String token);

    User findByEmail(String email);

    boolean isAdmin(User user);

    void assignAdminRoleAndActivate(String name, String email, String password);

    void validateNotAdminBeforeInvitation(String inviteeEmail);

    User getCurrentUser();
}
