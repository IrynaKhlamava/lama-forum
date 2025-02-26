package com.company.service.impl;

import com.company.dto.TopicDto;
import com.company.model.Topic;
import com.company.model.User;
import com.company.repository.TopicRepository;
import com.company.service.TopicService;
import com.company.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
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
    public List<Topic> getTopicsByPage(int page, int size) {
        return topicRepository.findAll(page, size);
    }

    @Override
    public long getTotalTopics() {
        return topicRepository.countTopics();
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
                .build() ;
    }

    @Override
    public void save(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    @Transactional
    public void archiveTopic(Long id, String userEmail) {
        Topic topic = topicRepository.findById(id).orElse(null);

        User currentUser = userService.findByEmail(userEmail);

        if (!topic.getUser().getId().equals(currentUser.getId()) && !userService.isAdmin(currentUser)) {
            throw new AccessDeniedException("You are not allowed to archive this topic");
        }

        topic.setArchived(true);
        topicRepository.save(topic);
        logger.info("Topic with the id:{} has been archived by the user: {})", id, userEmail);
    }

    @Override
    @Transactional
    public void updateTopic(Topic updatedTopic, String email) {
        Topic existingTopic = topicRepository.findById(updatedTopic.getId())
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        User currentUser = userService.findByEmail(email);

        if (!existingTopic.getUser().getId().equals(currentUser.getId()) && !userService.isAdmin(currentUser)) {
            throw new AccessDeniedException("You are not allowed to edit this topic");
        }

        existingTopic.setTitle(updatedTopic.getTitle());
        existingTopic.setContent(updatedTopic.getContent());
        topicRepository.save(existingTopic);
        logger.info("Topic with the id:{} has been updated by the user: {})", existingTopic.getId(), email);
    }

    @Override
    @Transactional
    public Topic findById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found with id: " + id));
    }

    public void validateEditPermissions(Topic topic, User currentUser) {
        if (currentUser == null || (!topic.getUser().getId().equals(currentUser.getId()) && !userService.isAdmin(currentUser))) {
            throw new AccessDeniedException("You are not allowed to edit this topic");
        }
    }

}
