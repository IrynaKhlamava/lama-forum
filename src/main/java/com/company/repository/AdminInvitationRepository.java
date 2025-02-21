package com.company.repository;

import com.company.model.AdminInvitation;
import java.util.Optional;

public interface AdminInvitationRepository {

    void save(AdminInvitation invitation);

    Optional<AdminInvitation> findByToken(String token);

    Optional<AdminInvitation> findByEmail(String email);

    void delete(AdminInvitation adminInvitation);
}
