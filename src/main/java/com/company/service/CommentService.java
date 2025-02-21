package com.company.service;

import java.security.Principal;

public interface CommentService {

    void addComment(Long topicId, String content, Principal principal);

    void deleteCommentById(Long commentId);
}
