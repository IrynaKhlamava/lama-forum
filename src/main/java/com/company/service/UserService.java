package com.company.service;

import com.company.dto.UserDto;
import com.company.dto.UserTopicAccessDto;
import com.company.model.Topic;
import com.company.model.User;

import java.util.Optional;

public interface UserService{

    String registerUser(UserDto userDto);

    String activateUser(String token);

    User findByEmail(String email);

    boolean isAdmin(User user);

    void assignAdminRoleAndActivate(String name, String email, String password);

    void validateNotAdminBeforeInvitation(String inviteeEmail);

    Optional<User> getCurrentUser();

    UserTopicAccessDto getUserTopicAccess(Topic topic);

}