package com.company.repository.impl;

import com.company.model.Comment;
import com.company.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void save(Comment comment) {
        entityManager.persist(comment);
    }

    @Override
    public void deleteCommentById(Long id) {
        entityManager.createQuery("DELETE FROM Comment c WHERE c.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Optional<Comment> findById(Long commentId) {
        return entityManager.createQuery("SELECT c FROM Comment c WHERE c.id = :commentId", Comment.class)
                .setParameter("commentId", commentId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean existsById(Long commentId) {
        return entityManager.createQuery("SELECT 1 FROM Comment c WHERE c.id = :id")
                .setParameter("id", commentId)
                .getResultStream()
                .findFirst()
                .isPresent();
    }

}
