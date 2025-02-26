package com.company.service.impl;

import com.company.model.Role;
import com.company.model.User;
import com.company.model.enumType.RoleName;
import com.company.repository.UserRepository;
import com.company.service.EmailService;
import com.company.service.JwtService;
import com.company.service.RoleService;
import com.company.service.UserService;
import com.company.service.exception.UserRegistrationException;
import com.company.service.exception.UserServiceImplException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Override
    @Transactional
    public String registerUser(User user) {

        validateUserRegistration(user);

        initializeNewUser(user);

        save(user);

        logger.info("New user registered (Email: {})", user.getEmail());

        sendActivationEmail(user);

        logger.debug("Activation email sent to {}", user.getEmail());

        return "Registration successful! Please check your email to activate your account";
    }

    private void validateUserRegistration(User user) {
        if (isUsernameAlreadyInUse(user.getName())) {
            logger.warn("Registration failed: Username already in use (Username: {})", user.getName());
            throw new UserRegistrationException("Username already in use");
        }

        if (isEmailAlreadyInUse(user.getEmail())) {
            logger.warn("Registration failed: Email already in use (Email: {})", user.getEmail());
            throw new UserRegistrationException("Email already in use");
        }
    }

    private boolean isUsernameAlreadyInUse(String name) {
        return userRepository.findByUserName(name).isPresent();
    }

    private boolean isEmailAlreadyInUse(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private void initializeNewUser(User user) {
        user.setPassword(hashPassword(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(false);
        user.setRoles(Set.of(getRoleByName(RoleName.USER)));
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private Role getRoleByName(RoleName roleName) {
        return roleService.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role " + roleName + " not found"));
    }

    private void sendActivationEmail(User user) {
        String token = jwtService.generateToken(user.getEmail());
        emailService.prepareAndSendEmail(user.getEmail(), token, RoleName.USER);
        logger.debug("Activation email sent to {}", user.getEmail());
    }

    @Override
    @Transactional
    public boolean activateUser(String token) {
        if (!isTokenValid(token)) {
            logger.warn("Activation failed: Invalid or expired token");
            return false;
        }

        String email = extractEmailFromToken(token);
        User user = findByEmail(email);

        if (isValid(user) && isNotActivated(user)) {
            activateUserAccount(user);
            logger.info("User activated successfully (Email: {})", email);
        }
        return true;
    }

    private boolean isTokenValid(String token) {
        return jwtService.validateToken(token);
    }

    private String extractEmailFromToken(String token) {
        return jwtService.extractEmail(token);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean isValid(User user) {
        if (user == null) {
            logger.warn("User by email was not found");
            throw new EntityNotFoundException("User not found for activation");
        }
        return true;
    }

    private boolean isNotActivated(User user) {
        if (user.isActive()) {
            logger.warn("User already activated");
            throw new IllegalStateException("User is already activated");
        }
        return true;
    }

    private void activateUserAccount(User user) {
        user.setActive(true);
        save(user);
    }

    public boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ADMIN);
    }

    private void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void assignAdminRoleAndActivate(String name, String email, String password) {
        User user = findByEmail(email);
        user.setName(name);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(hashPassword(password));

        Role adminRole = getRoleByName(RoleName.ADMIN);

        user.getRoles().add(adminRole);
        activateUserAccount(user);
    }

    @Override
    public void validateNotAdminBeforeInvitation(String inviteeEmail) {
        User existingUser = findByEmail(inviteeEmail);
        if (existingUser != null) {
            if (isAdmin(existingUser)) {
                logger.warn("User {} is already an admin", inviteeEmail);
                throw new UserServiceImplException("User is already an admin");
            }
        }
    }

    @Override
    @Transactional
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        return null;
    }

}