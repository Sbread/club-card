package com.t1project.club_card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new ClubMember());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(ClubMember clubMember) {
        clubMember.setPassword(passwordEncoder.encode(clubMember.getPassword()));
        clubMember.setRole("ROLE_MEMBER");
        clubMemberRepository.save(clubMember);
        return "redirect:/login";
    }
}
