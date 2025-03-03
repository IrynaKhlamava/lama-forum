package com.company.controller;

import com.company.dto.TopicDto;
import com.company.dto.UserTopicAccessDto;
import com.company.model.Topic;
import com.company.model.User;
import com.company.service.TopicService;
import com.company.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String showCreateTopicForm(Model model) {
        model.addAttribute("topicDto", new TopicDto());
        return "create-topic";
    }

    @PostMapping("/create")
    public String createTopic(@Valid @ModelAttribute("topicDto") TopicDto topicDto,
                              Principal principal,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("topicDto", topicDto);
            return "create-topic";
        }
        topicService.createAndSaveTopic(topicDto, principal);
        redirectAttributes.addFlashAttribute("message", "New Topic has been created");
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public ModelAndView viewTopic(@PathVariable("id") Long id) {
        Topic topic = topicService.findById(id);
        UserTopicAccessDto userTopicAccess = userService.getUserTopicAccess(topic);

        ModelAndView modelAndView = new ModelAndView("topic");
        modelAndView.addObject("topic", topic);
        modelAndView.addObject("isArchived", topic.isArchived());
        modelAndView.addObject("userTopicAccess", userTopicAccess);

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

        User currentUser = userService.getCurrentUser().orElse(null);

        topicService.validateEditPermissions(topic, currentUser);

        return new ModelAndView("edit-topic", "topic", topic);
    }

    @PostMapping("/update")
    public String updateTopic(@ModelAttribute("topic") @Valid TopicDto topicDto,
                              BindingResult result,
                              Principal principal,
                              Model model) {

        if (result.hasErrors()) {
            model.addAttribute("topic", topicDto);
            return "edit-topic";
        }

        topicService.updateTopic(topicDto, principal.getName());
        return "redirect:/topics/" + topicDto.getId();
    }

}
