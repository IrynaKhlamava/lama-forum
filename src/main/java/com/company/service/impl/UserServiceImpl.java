package com.company.service.impl;

import com.company.dto.UserDto;
import com.company.dto.UserTopicAccessDto;
import com.company.model.Role;
import com.company.model.Topic;
import com.company.model.User;
import com.company.model.enumType.RoleName;
import com.company.repository.UserRepository;
import com.company.service.EmailService;
import com.company.service.JwtService;
import com.company.service.RoleService;
import com.company.service.UserService;
import com.company.service.exception.ResourceNotFoundException;
import com.company.service.exception.UserServiceImplException;

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
import java.util.HashSet;
import java.util.Optional;
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
    public String registerUser(UserDto userDto) {
        Optional<String> validationMessage = validateUserRegistration(userDto);

        if (validationMessage.isPresent()) {
            return validationMessage.get();
        }

        processUserRegistration(userDto);

        return "Registration successful! Please check your email to activate your account";
    }

    private void processUserRegistration(UserDto userDto) {

        User user = initializeNewUser(userDto);

        save(user);

        logger.info("New user registered (Email: {})", user.getEmail());

        sendActivationEmail(user);

    }

    private Optional<String> validateUserRegistration(UserDto userDto) {
        if (isUsernameAlreadyInUse(userDto.getName())) {
            logger.warn("Registration failed: Username already in use (Username: {})", userDto.getName());
            return Optional.of("Username is already in use");
        }

        if (isEmailAlreadyInUse(userDto.getEmail())) {
            logger.warn("Registration failed: Email already in use (Email: {})", userDto.getEmail());
            return Optional.of("Email is already registered");
        }

        return Optional.empty();
    }

    private boolean isUsernameAlreadyInUse(String name) {
        return userRepository.findByUserName(name).isPresent();
    }

    private boolean isEmailAlreadyInUse(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private User initializeNewUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(hashPassword(userDto.getPassword()))
                .createdAt(LocalDateTime.now())
                .isActive(false)
                .roles(Set.of(getRoleByName(RoleName.USER)))
                .build();
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private Role getRoleByName(RoleName roleName) {
        return roleService.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role " + roleName + " not found"));
    }

    private void sendActivationEmail(User user) {
        String token = jwtService.generateToken(user.getEmail());
        emailService.prepareAndSendEmail(user.getEmail(), token, RoleName.USER);
    }

    @Override
    @Transactional
    public String activateUser(String token) {
        if (!isTokenValid(token)) {
            logger.warn("Activation failed: Invalid or expired token");
            return "Activation failed: Invalid or expired token";
        }

        String email = extractEmailFromToken(token);
        User user = findByEmail(email);

        if (isValid(user) && isNotActivated(user)) {
            activateUserAccount(user);
            logger.info("User activated successfully (Email: {})", email);
        }
        return "Account activated successfully! You can login now";
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
            throw new UserServiceImplException("User not found for activation");
        }
        return true;
    }

    private boolean isNotActivated(User user) {
        if (user.isActive()) {
            logger.warn("User is already activated");
            throw new UserServiceImplException("User is already activated");
        }
        return true;
    }

    private void activateUserAccount(User user) {
        user.setActive(true);
        save(user);
    }

    public boolean isAdmin(User user) {
        if (user == null || user.getRoles() == null) {
            return false;
        }
        return user.getRoles().stream()
                .anyMatch(role -> role.getName()== RoleName.ADMIN);
    }

    private void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void assignAdminRoleAndActivate(String name, String email, String password) {
        checkUsername(name);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setPassword(hashPassword(password));
            newUser.setRoles(new HashSet<>());
            return newUser;
        });

        user.setName(name);

        Role adminRole = getRoleByName(RoleName.ADMIN);
        user.getRoles().add(adminRole);

        activateUserAccount(user);
        userRepository.save(user);
    }

    private void checkUsername(String name) {
        if (isUsernameInUse(name)) {
            throw new UserServiceImplException("Username: '" + name + "' already in use");
        }
    }

    private boolean isUsernameInUse(String name) {
        return userRepository.isUsernameInUse(name);
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
    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            return userRepository.findByEmail(userDetails.getUsername());
        }
        return Optional.empty();
    }

    @Transactional
    public UserTopicAccessDto getUserTopicAccess(Topic topic) {
        Optional<User> optionalUser = getCurrentUser();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean isAdmin = isAdmin(user);
            boolean canEdit = (!topic.isArchived() && topic.getUser().getId().equals(user.getId())) || isAdmin;
            boolean canComment = !topic.isArchived() || isAdmin;

            return new UserTopicAccessDto(user.getName(), canEdit, canComment, isAdmin);
        }

        return new UserTopicAccessDto(null, false, false, false);
    }

}