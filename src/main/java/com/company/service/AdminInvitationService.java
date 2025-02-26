package com.company.service;

import com.company.dto.AdminAccountDto;
import com.company.model.AdminInvitation;

import java.security.Principal;
import java.util.Optional;

public interface AdminInvitationService {
    
    String inviteAdmin(String email, Principal principal);

    boolean finalizeAdminInvitation(AdminAccountDto adminAccountDto);

    void validateInvitationToken(String token);
}
