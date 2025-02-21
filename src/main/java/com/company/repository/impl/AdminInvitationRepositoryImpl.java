package com.company.repository.impl;

import com.company.model.AdminInvitation;
import com.company.repository.AdminInvitationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdminInvitationRepositoryImpl implements AdminInvitationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(AdminInvitation invitation) {
        entityManager.persist(invitation);
    }

    @Override
    public Optional<AdminInvitation> findByToken(String token) {
        return entityManager.createQuery(
                        "SELECT a FROM AdminInvitation a WHERE a.token = :token", AdminInvitation.class)
                .setParameter("token", token)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<AdminInvitation> findByEmail(String email) {
        return entityManager.createQuery("SELECT a FROM AdminInvitation a WHERE a.email = :email", AdminInvitation.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void delete(AdminInvitation adminInvitation) {
        entityManager.createQuery(
                        "DELETE FROM AdminInvitation a where a.id =:id ")
                .setParameter("id", adminInvitation.getId())
                .executeUpdate();
    }

}
