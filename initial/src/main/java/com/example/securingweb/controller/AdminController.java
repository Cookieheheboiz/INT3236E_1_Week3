package com.example.securingweb.controller;

import com.example.securingweb.entity.User;
import com.example.securingweb.service.CustomUserDetailsService;
import com.example.securingweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping
    public String adminHome(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleUserStatus(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public String promoteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && !user.getRoles().contains("ADMIN")) {
            user.setRoles(user.getRoles() + ",ADMIN");
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }
}
