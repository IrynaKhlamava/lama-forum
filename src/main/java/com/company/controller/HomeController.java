package com.company.controller;

import com.company.dto.HomePageDto;
import com.company.service.HomeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ModelAndView home(@RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "size", defaultValue = "10") int size,
                             @ModelAttribute("message") String message) {

        ModelAndView modelAndView = new ModelAndView("home");

        HomePageDto homePageData = homeService.getHomePageData(page, size, message);

        modelAndView.addObject("homePage", homePageData);

        return modelAndView;
    }

}

