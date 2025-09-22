package com.example.securingweb.controller;

import com.example.securingweb.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               @RequestParam(value = "role", defaultValue = "USER") String role,
                               Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("username", username); // Preserve form data
            return "register";
        }

        if (username == null || username.trim().length() < 3) {
            model.addAttribute("error", "Username must be at least 3 characters long");
            return "register";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long");
            model.addAttribute("username", username);
            return "register";
        }

        if (userDetailsService.userExists(username.trim())) {
            model.addAttribute("error", "Username already exists");
            model.addAttribute("username", username);
            return "register";
        }

        try {
            String validRole = validateRole(role);

            userDetailsService.createUser(username.trim(), password, validRole);

            String successMessage = "Registration successful! " +
                    "User '" + username + "' created with role '" + validRole + "'. " +
                    "You can now login.";
            model.addAttribute("success", successMessage);

            return "register";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("username", username);
            return "register";
        }
    }

    private String validateRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "USER"; // Default role
        }

        String normalizedRole = role.trim().toUpperCase();

        switch (normalizedRole) {
            case "ADMIN":
                return "ADMIN";
            case "USER":
            default:
                return "USER";
        }
    }
}
