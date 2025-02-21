package com.company.controller;

import com.company.model.User;
import com.company.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               Model model) {

        String message = userService.registerUser(user);

        model.addAttribute("message", message);

        return "register-result";
    }

    @GetMapping("/register-result")
    public String showRegistrationSuccessPage() {
        return "register-result";
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token,
                                  Model model) {
        boolean activated = userService.activateUser(token);

        model.addAttribute("message", activated ? "Account activated successfully!" : "Activation failed");

        return "activation-result";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

}
