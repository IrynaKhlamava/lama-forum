package com.company.service;

import com.company.dto.AdminAccountDto;
import java.security.Principal;

public interface AdminInvitationService {
    
    void inviteAdmin(String email, Principal principal);

    boolean finalizeAdminInvitation(AdminAccountDto adminAccountDto);

    void validateInvitationToken(String token);
}
