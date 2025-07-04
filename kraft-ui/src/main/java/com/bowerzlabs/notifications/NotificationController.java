package com.bowerzlabs.notifications;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/notifications")
public class NotificationController {

    // notifications page
    @GetMapping
    public String renderNotifications(Model model){
        return "kraft-notifications/index";
    }
}
