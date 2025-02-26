package com.company.controller;

import com.company.dto.AdminAccountDto;
import com.company.service.AdminInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminInvitationController {

    private final AdminInvitationService invitationService;

    @GetMapping("/invite")
    public String showInviteAdminForm() {
        return "invite-admin";
    }

    @PostMapping("/invite")
    public String inviteAdmin(@RequestParam("email") String email,
                              Principal principal,
                              Model model) {

        invitationService.inviteAdmin(email, principal);
        model.addAttribute("message", "Invitation sent to " + email);
        return "invite-admin";
    }

    @GetMapping("/set-admin-account")
    public String showSetAdminAccountForm(@RequestParam("token") String token, Model model) {
        invitationService.validateInvitationToken(token);

        model.addAttribute("adminAccountDto", new AdminAccountDto(token));
        return "set-admin-account";
    }

    @PostMapping("/set-admin-account")
    public String setAdminAccount(@Valid @ModelAttribute("adminAccountDto") AdminAccountDto adminAccountDto,
                                  RedirectAttributes redirectAttributes) {

        boolean success = invitationService.finalizeAdminInvitation(adminAccountDto);

        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token");
            return "redirect:/users/login";
        }

        redirectAttributes.addFlashAttribute("message", "Password set successfully! You can login now");
        return "redirect:/users/login";
    }

}