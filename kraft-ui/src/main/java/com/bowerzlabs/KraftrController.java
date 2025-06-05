package com.bowerzlabs;

import com.bowerzlabs.analytics.AnalyticsComponent;
import com.bowerzlabs.analytics.BarChart;
import com.bowerzlabs.analytics.LineGraph;
import com.bowerzlabs.analytics.PieCharts;
import com.bowerzlabs.constants.Status;
import com.bowerzlabs.events.UIEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class KraftrController {
    private final ApplicationEventPublisher applicationEventPublisher;

    public KraftrController(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // render dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
            List<AnalyticsComponent> components = List.of(
                    new BarChart("Revenue Chart", "Monthly revenue breakdown"),
                    new PieCharts("Monthly Users", "Users per month"),
                    new LineGraph("Daily Traffic", "Traffic patterns")
                    // Add more here
            );

            model.addAttribute("components", components);
            model.addAttribute("totalBooks", 150);
//        model.addAttribute("totalBooks", 150);
            model.addAttribute("activeUsers", 30);
            model.addAttribute("pendingReturns", 5);
            return "dashboard";
    }

    @GetMapping("/test")
    public String test(){
        System.out.println("🔥 /admin/test hit!");
        return "test";
    }




}
