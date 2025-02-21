package com.company.service.impl;

import com.company.model.Comment;
import com.company.model.Topic;
import com.company.model.User;
import com.company.repository.CommentRepository;
import com.company.service.CommentService;
import com.company.service.TopicService;
import com.company.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final UserService userService;

    private final TopicService topicService;

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void addComment(Long topicId, String content, Principal principal) {
        Comment comment = createComment(topicId, content, principal);
        saveComment(comment);
    }

    @Override
    @Transactional
    public void deleteCommentById(Long commentId) {
        if(commentRepository.existsById(commentId)) {
            commentRepository.deleteCommentById(commentId);
            logger.info("Comment with Id : {} has been deleted",  commentId);
        } else {
            logger.info("Comment not found with Id : {} ",  commentId);
            throw new EntityNotFoundException("Comment not found with id: " + commentId);
        }
    }

    private void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    private Comment createComment(Long topicId, String content, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Topic topic = topicService.findById(topicId);
        return Comment.builder()
                .user(user)
                .topic(topic)
                .content(content.trim())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
