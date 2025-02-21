package com.company.controller;

import com.company.model.Topic;
import com.company.model.User;
import com.company.service.TopicService;
import com.company.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final UserService userService;

    private final TopicService topicService;

    @GetMapping
    public ModelAndView home(@RequestParam(name ="page", defaultValue = "1") int page,
                             @RequestParam(name ="size",defaultValue = "10") int size) {
        ModelAndView modelAndView = new ModelAndView("home");

        List<Topic> topics = topicService.getTopicsByPage(page, size + 1);
        boolean hasNext = topics.size() > size;

        if (hasNext) {
            topics.remove(topics.size() - 1);
        }

        modelAndView.addObject("topics", topics);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("size", size);
        modelAndView.addObject("hasNext", hasNext);
        modelAndView.addObject("hasPrev", page > 1);

        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            modelAndView.addObject("username", currentUser.getName());
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("isAdmin", userService.isAdmin(currentUser));
        } else {
            modelAndView.addObject("username", null);
            modelAndView.addObject("currentUser", null);
            modelAndView.addObject("isAdmin", false);
        }

        return modelAndView;
    }

}

