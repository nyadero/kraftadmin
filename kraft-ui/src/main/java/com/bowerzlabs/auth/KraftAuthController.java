package com.bowerzlabs.auth;

import com.bowerzlabs.annotations.AdminController;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@AdminController
@Slf4j
@Controller
@RequestMapping("/admin")
public class KraftAuthController {

    private static final Logger log = LoggerFactory.getLogger(KraftAuthController.class);

    // render login page
    @GetMapping("/auth/login")
    public String loginPage(Model model, HttpServletRequest request) {
        log.info("Inside login admin");
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
