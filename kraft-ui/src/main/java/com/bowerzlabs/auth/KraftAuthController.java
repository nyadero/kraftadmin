package com.bowerzlabs.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/auth")
public class KraftAuthController {

    // render login page
    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) {
        // Detect login errors or logout success
        String error = request.getParameter("error");
        String logout = request.getParameter("logout");
        String expired = request.getParameter("expired");

        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }

        if (expired != null) {
            model.addAttribute("error", "Session expired. Please login again.");
        }

        return "auth/login";
    }
}
