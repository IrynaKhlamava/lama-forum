package com.company.service;

import com.company.dto.TopicDto;
import com.company.model.Topic;
import com.company.model.User;

import java.security.Principal;
import java.util.List;

public interface TopicService {

    List<Topic> getTopicsByPage(int page, int size);

    void createAndSaveTopic(TopicDto topicDto, Principal principal);

    Topic findById(Long id);

    void save(Topic topic);

    void archiveTopic(Long id, String name);

    void updateTopic(TopicDto topicDto, String userEmail);

    void validateEditPermissions(Topic topic, User currentUser);

    Topic refreshTopicCache(Long topicId);
}
