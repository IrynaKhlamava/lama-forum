package com.company.repository;

import com.company.model.Comment;

import java.util.Optional;

public interface CommentRepository {

    void save(Comment comment);

    void deleteCommentById(Long topicId);

    Optional<Comment> findById(Long commentId);

    boolean existsById(Long commentId);
}
