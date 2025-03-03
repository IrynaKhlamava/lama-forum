package com.company.service.impl;

import com.company.dto.AdminAccountDto;
import com.company.model.AdminInvitation;
import com.company.model.User;
import com.company.model.enumType.RoleName;
import com.company.repository.AdminInvitationRepository;
import com.company.service.AdminInvitationService;
import com.company.service.EmailService;
import com.company.service.UserService;
import com.company.service.exception.AdminInvitationImplServiceException;
import com.company.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
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
    public void inviteAdmin(String inviteeEmail, Principal principal) {

        String inviterEmail = principal.getName();

        User inviter = userService.findByEmail(inviterEmail);

        logger.info("Admin with email {} invites a new admin with email: {}", inviterEmail, inviteeEmail);

        validateIfActiveInvitationExist(inviteeEmail);

        userService.validateNotAdminBeforeInvitation(inviteeEmail);

        String token = UUID.randomUUID().toString();
        AdminInvitation invitation = createAdminInvitation(inviteeEmail, inviter, token);

        invitationRepository.save(invitation);

        sendInvitationByEmail(inviteeEmail, token);

        logger.info("Invitation for new admin sent to {}", inviteeEmail);

    }

    private void validateIfActiveInvitationExist(String inviteeEmail) {
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

    private void sendInvitationByEmail(String inviteeEmail, String token) {
        emailService.prepareAndSendEmail(inviteeEmail, token, RoleName.ADMIN);

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
    public boolean finalizeAdminInvitation(AdminAccountDto adminAccountDto) {

        String token = adminAccountDto.getToken();

        logger.info("Processing admin invitation with token: {}", token);

        AdminInvitation invitation = findOrThrowInvitationByToken(token);

        userService.assignAdminRoleAndActivate(adminAccountDto.getName(), invitation.getEmail(), adminAccountDto.getPassword());

        logger.info("Admin invitation accepted. User with email {} is now an admin.", invitation.getEmail());

        invitationRepository.delete(invitation);

        logger.info("New admin by invitation was created. Admin invitation for email {} deleted from db", invitation.getEmail());

        return true;

    }

    private Optional<AdminInvitation> findInvitationByToken(String token) {
        return invitationRepository.findByToken(token);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateInvitationToken(String token) {
        AdminInvitation invitation = findOrThrowInvitationByToken(token);

        checkExistedInvitation(invitation);

        logger.info("User with email {} accepts invitation by adminId {}", invitation.getEmail(), invitation.getInvitedBy().getId());
    }

    private AdminInvitation findOrThrowInvitationByToken(String token) {
        return findInvitationByToken(token)
                .orElseThrow(() -> {
                    logger.warn("Invalid admin invitation token");
                    return new ResourceNotFoundException("Invalid invitation token");
                });
    }

    private void checkExistedInvitation(AdminInvitation invitation) {
        if (invitation.isAccepted()) {
            logger.warn("Admin invitation already accepted for email: {}", invitation.getEmail());
            throw new AdminInvitationImplServiceException("Invitation already accepted");
        }

        if (isInvitationExpired(invitation)) {
            logger.warn("Admin invitation expired for email: {}", invitation.getEmail());
            throw new AdminInvitationImplServiceException("Invitation already expired");
        }
    }
}

