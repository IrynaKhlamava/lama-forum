package com.company.controller;

import com.company.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public String addComment(@RequestParam("topicId") Long topicId,
                             @RequestParam("content") String content,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        commentService.addComment(topicId, content, principal);

        redirectAttributes.addFlashAttribute("message", "Comment added successfully!");

        return "redirect:/topics/" + topicId;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteComment(@PathVariable("id") Long commentId,
                                @RequestParam("topicId") Long topicId,
                                RedirectAttributes redirectAttributes) {

        commentService.deleteCommentById(commentId);

        redirectAttributes.addFlashAttribute("message", "Comment deleted successfully");

        return "redirect:/topics/" + topicId;
    }
}
