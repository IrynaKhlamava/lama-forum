package com.company.service;

import com.company.model.Topic;
import com.company.model.User;

import java.security.Principal;
import java.util.List;

public interface TopicService {

    List<Topic> getTopicsByPage(int page, int size);

    void createAndSaveTopic(String title, String content, Principal principal);

    Topic findById(Long id);

    void save(Topic topic);

    void archiveTopic(Long id, String name);

    void updateTopic(Topic topic, String name);

    void validateEditPermissions(Topic topic, User currentUser);

    long getTotalTopics();
}
