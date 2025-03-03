package com.company.service.impl;

import com.company.dto.HomePageDto;
import com.company.model.Topic;
import com.company.model.User;
import com.company.service.HomeService;
import com.company.service.TopicService;
import com.company.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final TopicService topicService;

    private final UserService userService;

    public HomePageDto getHomePageData(int page, int size, String message) {

        List<Topic> topics = topicService.getTopicsByPage(page, size + 1);

        boolean hasNext = topics.size() > size;

        if (hasNext) {
            topics.remove(topics.size() - 1);
        }

        User currentUser = userService.getCurrentUser().orElse(new User());

        return new HomePageDto(
                topics, page, size, hasNext, page > 1, message, currentUser.getUsername(), currentUser, userService.isAdmin(currentUser)
        );
    }
}

