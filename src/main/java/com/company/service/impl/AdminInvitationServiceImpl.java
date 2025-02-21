package com.company.service.impl;

import com.company.model.AdminInvitation;
import com.company.model.User;
import com.company.repository.AdminInvitationRepository;
import com.company.service.AdminInvitationService;
import com.company.service.EmailService;
import com.company.service.UserService;
import com.company.service.exception.AdminInvitationImplServiceException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminInvitationServiceImpl implements AdminInvitationService {

    private static final Logger logger = LoggerFactory.getLogger(AdminInvitationServiceImpl.class);

    private final AdminInvitationRepository invitationRepository;

    private final UserService userService;

    private final EmailService emailService;

    @Transactional
    public String inviteAdmin(String inviteeEmail, String inviterEmail) {

        User inviter = userService.findByEmail(inviterEmail);

        logger.info("Admin with email {} invites a new admin with email: {}", inviterEmail, inviteeEmail);

        validateInviteeEmailAndInvitation(inviteeEmail);

        userService.validateNotAdminBeforeInvitation(inviteeEmail);

        String token = UUID.randomUUID().toString();
        AdminInvitation invitation = createAdminInvitation(inviteeEmail, inviter, token);

        invitationRepository.save(invitation);

        sendInviteByEmail(inviteeEmail, token);

        logger.info("Invitation for new admin sent to {}", inviteeEmail);

        return "Invitation sent!";
    }

    private void validateInviteeEmailAndInvitation(String inviteeEmail) {
        Optional<AdminInvitation> existingInvitation = invitationRepository.findByEmail(inviteeEmail);
        existingInvitation.ifPresent(this::handleExistingInvitation);
    }

    private void handleExistingInvitation(AdminInvitation invitation) {
        if (!isInvitationExpired(invitation)) {
            logger.info("An active invitation has already been sent to email {}", invitation.getEmail());
            throw new AdminInvitationImplServiceException("An active invitation has already been sent to this email");
        } else {
            invitationRepository.delete(invitation);
            logger.info("Expired admin invitation for email: {} has been deleted", invitation.getEmail());
        }
    }

    private boolean isInvitationExpired(AdminInvitation invitation) {
        return invitation.getExpirationDate().isBefore(LocalDateTime.now());
    }

    private void sendInviteByEmail(String inviteeEmail, String token) {
        String invitationLink = "http://localhost:8080/admins/accept?token=" + token;
        emailService.sendEmail(inviteeEmail, "Admin Invitation", "Click here to set your password: " + invitationLink);
    }

    private AdminInvitation createAdminInvitation(String inviteeEmail, User inviter, String token) {
        return AdminInvitation.builder()
                .email(inviteeEmail)
                .token(token)
                .invitedBy(inviter)
                .expirationDate(LocalDateTime.now().plusDays(7))
                .build();
    }

    @Transactional
    public boolean finalizeAdminInvitation(String name, String token, String password) {

        logger.info("Processing admin invitation with token: {}", token);

        AdminInvitation invitation = findOrThrowByToken(token);

        checkInvitation(invitation);

        userService.createAndActivateAdminAccount(name, invitation.getEmail(), password);

        logger.info("Admin invitation accepted. User with email {} is now an admin.", invitation.getEmail());

        invitationRepository.delete(invitation);

        logger.info("New admin by invitation was created. Admin invitation for email {} deleted from db", invitation.getEmail());

        return true;

    }

    @Override
    public Optional<AdminInvitation> getOptionalInvitationByToken(String token) {
        return invitationRepository.findByToken(token);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateInvitation(String token) {
        AdminInvitation invitation = getOptionalInvitationByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invitation by token not found"));

        if (isInvitationExpired(invitation)) {
            throw new IllegalStateException("Invitation token has expired");
        }

        logger.info("User with email {} accepts invitation by adminId {}", invitation.getEmail(), invitation.getInvitedBy());
    }

    private AdminInvitation findOrThrowByToken(String token) {
        return getOptionalInvitationByToken(token)
                .orElseThrow(() -> {
                    logger.warn("Invalid admin invitation token");
                    return new EntityNotFoundException("Invalid invitation token");
                });
    }

    private void checkInvitation(AdminInvitation invitation) {
        if (invitation.isAccepted()) {
            logger.warn("Admin invitation already accepted for email: {}", invitation.getEmail());
            throw new IllegalStateException("Invitation already accepted");
        }

        if (isInvitationExpired(invitation)) {
            logger.warn("Admin invitation expired for email: {}", invitation.getEmail());
            throw new IllegalStateException("Invitation already expired");
        }
    }
}

