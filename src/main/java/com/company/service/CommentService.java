package com.company.service;

import com.company.dto.CommentDto;

import java.security.Principal;

public interface CommentService {

    void addComment(Long topicId, CommentDto commentDto, Principal principal);

    void deleteCommentById(Long commentId, Long topicId);
}
