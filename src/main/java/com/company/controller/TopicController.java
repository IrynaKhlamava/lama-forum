package com.company.controller;

import com.company.model.Topic;
import com.company.model.User;
import com.company.service.TopicService;
import com.company.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;

    private final UserService userService;

    @GetMapping("/create")
    public String showCreateTopicForm() {
        return "create-topic";
    }

    @PostMapping("/create")
    public String createTopic(@RequestParam("title") String title,
                              @RequestParam("content") String content,
                              Principal principal) {

        topicService.createAndSaveTopic(title, content, principal);

        return "redirect:/";
    }

    @GetMapping("/{id}")
    public ModelAndView viewTopic(@PathVariable("id") Long id) {
        Topic topic = topicService.findById(id);

        ModelAndView modelAndView = new ModelAndView("topic");
        modelAndView.addObject("topic", topic);
        modelAndView.addObject("isArchived", topic.isArchived());

        User currentUser = userService.getCurrentUser();
        boolean isAdmin = userService.isAdmin(currentUser);

        if (currentUser != null) {
            modelAndView.addObject("username", currentUser.getName());
            modelAndView.addObject("canEdit", (!topic.isArchived() && topic.getUser().getId().equals(currentUser.getId())) || isAdmin);
            modelAndView.addObject("canComment", !topic.isArchived() || isAdmin);
            modelAndView.addObject("isAdmin", isAdmin);
        } else {
            modelAndView.addObject("username", null);
            modelAndView.addObject("canEdit", false);
            modelAndView.addObject("canComment", false);
            modelAndView.addObject("isAdmin", false);
        }

        return modelAndView;
    }


    @PostMapping("/archive/{id}")
    public String archiveTopic(@PathVariable("id") Long id,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {

        topicService.archiveTopic(id, principal.getName());

        redirectAttributes.addFlashAttribute("message", "Topic has been archived successfully");

        return "redirect:/topics/" + id;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable("id") Long id) {

        Topic topic = topicService.findById(id);

        User currentUser = userService.getCurrentUser();

        topicService.validateEditPermissions(topic, currentUser);

        return new ModelAndView("edit-topic", "topic", topic);
    }

    @PostMapping("/update")
    public String updateTopic(@ModelAttribute("topic") Topic topic,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {

        topicService.updateTopic(topic, principal.getName());
        redirectAttributes.addFlashAttribute("message", "Topic has been updated successfully");

        return "redirect:/topics/" + topic.getId();
    }

}
