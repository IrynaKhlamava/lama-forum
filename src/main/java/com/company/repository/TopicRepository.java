package com.company.repository;

import com.company.model.Topic;

import java.util.List;
import java.util.Optional;

public interface TopicRepository {

    void save(Topic topic);

    Optional<Topic> findById(Long topicId);

    List<Topic> findAll(int page, int size);

    long countTopics();

}
