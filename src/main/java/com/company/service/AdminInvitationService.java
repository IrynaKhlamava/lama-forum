package com.company.service;

import com.company.model.AdminInvitation;

import java.util.Optional;

public interface AdminInvitationService {
    
    String inviteAdmin(String email, String inviterEmail);

    Optional<AdminInvitation> getOptionalInvitationByToken(String token);

    boolean finalizeAdminInvitation(String name, String token, String password);

    void validateInvitation(String token);
}
