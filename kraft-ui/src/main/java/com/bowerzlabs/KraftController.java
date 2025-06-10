package com.bowerzlabs;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.service.CrudService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//@AdminController
@Controller
@RequestMapping("/admin")
public class KraftController {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CrudService crudService;

    public KraftController(ApplicationEventPublisher applicationEventPublisher, CrudService crudService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.crudService = crudService;
    }

    // render dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) throws Exception {
            com.bowerzlabs.AnalyticsComponent analyticsComponent = new com.bowerzlabs.AnalyticsComponent("Test component", "Description");
            new BarchartComponent(analyticsComponent);
            Page<DbObjectSchema> data = crudService.findAll("AdminUserActions", 0, 20, new HashMap<>(), new ArrayList<>());
            analyticsComponent.setData(Map.of("AdminUserActions", data.getTotalElements()));
            analyticsComponent.setData(Map.of("Jan", 42424, "feb", 55656));
            analyticsComponent.setData(Map.of("Jan", 42424, "feb", 886787, "march", 97866));
            analyticsComponent.setData(Map.of("Jan", 42424, "oct", 89797, "dec", 89786));
            return "dashboard";
    }

    @GetMapping("/test")
    public String test(){
        System.out.println("🔥 /admin/test hit!");
        return "test";
    }


}
