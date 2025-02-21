package com.company.controller;

import com.company.service.AdminInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/invite")
    public String showInviteAdminForm() {
        return "invite-admin";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/invite")
    public String inviteAdmin(@RequestParam("email") String email, Principal principal, RedirectAttributes redirectAttributes) {
        invitationService.inviteAdmin(email, principal.getName());
        redirectAttributes.addFlashAttribute("message", "Invitation sent to " + email);
        return "redirect:/";
    }

    @GetMapping("/accept")
    public String acceptInvitation(@RequestParam("token") String token, Model model) {

        invitationService.validateInvitation(token);

        model.addAttribute("token", token);
        return "set-admin-account";
    }

    @PostMapping("/set-admin-account")
    public String setAdminAccount(@RequestParam("token") String token,
                                  @RequestParam("name") String name,
                                  @RequestParam("password") String password,
                                  RedirectAttributes redirectAttributes) {

        boolean success = invitationService.finalizeAdminInvitation(name, token, password);

        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token");
            return "redirect:/admins/accept?token=" + token;
        }

        redirectAttributes.addFlashAttribute("message", "Password set successfully! You can login now");
        return "redirect:/users/login";
    }
}