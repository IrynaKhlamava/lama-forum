package com.company.repository.impl;

import com.company.model.Topic;

import com.company.repository.TopicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TopicRepositoryImpl implements TopicRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Topic topic) {
        entityManager.persist(topic);
    }

    @Override
    public Optional<Topic> findById(Long topicId) {
        return entityManager.createQuery(
                        "SELECT t FROM Topic t " +
                                "JOIN FETCH t.user u " +
                                "LEFT JOIN FETCH t.comments c " +
                                "LEFT JOIN FETCH c.user cu " +
                                "WHERE t.id = :topicId", Topic.class)
                .setParameter("topicId", topicId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Topic> findAll(int page, int pageSize) {
        List<Long> topicIds = entityManager.createQuery(
                        "SELECT t.id FROM Topic t ORDER BY t.createdAt DESC", Long.class)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return entityManager.createQuery(
                        "SELECT t FROM Topic t " +
                                "JOIN FETCH t.user u " +
                                "LEFT JOIN FETCH u.roles " +
                                "LEFT JOIN FETCH t.comments c " +
                                "LEFT JOIN FETCH c.user cu " +
                                "WHERE t.id IN :topicIds", Topic.class)
                .setParameter("topicIds", topicIds)
                .getResultList();
    }

    @Override
    public long countTopics() {
        return entityManager.createQuery("SELECT COUNT(t) FROM Topic t", Long.class)
                .getSingleResult();
    }

}
