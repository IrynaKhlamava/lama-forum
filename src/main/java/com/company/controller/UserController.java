package com.company.controller;

import com.company.dto.UserDto;
import com.company.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("userDto", new UserDto());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userDto") @Valid UserDto userDto,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            return "register-form";
        }

        String message = userService.registerUser(userDto);
        model.addAttribute("message", message);
        return "result";
    }

    @GetMapping("/result")
    public String showResultPage() {

        return "result";
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token,
                                  Model model) {

        String resultMessage = userService.activateUser(token);

        model.addAttribute("message", resultMessage);

        return "result";
    }

    @GetMapping("/login")
    public String showLoginPage() {

        return "login";
    }

}
