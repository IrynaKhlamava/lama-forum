package com.company.service.impl;

import com.company.dto.TopicDto;
import com.company.model.Topic;
import com.company.model.User;
import com.company.repository.TopicRepository;
import com.company.service.TopicService;
import com.company.service.UserService;
import com.company.service.exception.PermissionDeniedException;
import com.company.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TopicServiceImpl implements TopicService {

    private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

    private final UserService userService;

    private final TopicRepository topicRepository;

    @Override
    @Cacheable(value = "topics", key = "#page")
    public List<Topic> getTopicsByPage(int page, int size) {
        return topicRepository.findTopicsByPage(page, size);
    }

    @Override
    @Transactional
    public void createAndSaveTopic(TopicDto topicDto, Principal principal) {
        Topic createdTopic = createTopic(topicDto.getTitle(), topicDto.getContent(), principal);

        save(createdTopic);

        logger.info("New Topic has been created (Email: {})", principal.getName());
    }

    private Topic createTopic(String title, String content, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return Topic.builder()
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Override
    @CacheEvict(value = "topics", allEntries = true)
    public void save(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    @Transactional
    @CacheEvict(value = "topics", key = "#id")
    public void archiveTopic(Long id, String userEmail) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        User currentUser =userService.findByEmail(userEmail);

        validateEditPermissions(topic, currentUser);

        topic.setArchived(true);

        topicRepository.save(topic);

        logger.info("Topic with the id:{} has been archived by the user: {})", id, userEmail);
    }

    @Override
    @Transactional
    @CachePut(value = "topics", key = "#topicDto.id")
    public void updateTopic(TopicDto topicDto, String userEmail) {
        Topic topic = topicRepository.findById(topicDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        User user = userService.findByEmail(userEmail);

        validateEditPermissions(topic, user);

        topic.setTitle(topicDto.getTitle());
        topic.setContent(topicDto.getContent());

        topicRepository.save(topic);

        logger.info("Topic with the id:{} has been updated by the user: {})", topic.getId(), userEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public Topic findById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
    }

    public void validateEditPermissions(Topic topic, User currentUser) {
        if (currentUser == null || (!topic.getUser().getId().equals(currentUser.getId()) && !userService.isAdmin(currentUser))) {
            logger.warn("User without permissions tried to change topic with ID: {} ",  topic.getId());
            throw new PermissionDeniedException("You are not allowed to edit this topic");
        }
    }

    @CachePut(value = "topics", key = "#id")
    @CacheEvict(value = "topics", allEntries = true)
    @Transactional(readOnly = true)
    public Topic refreshTopicCache(Long id) {
        return findById(id);
    }

}
