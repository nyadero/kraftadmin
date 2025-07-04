package com.bowerzlabs;

import com.bowerzlabs.service.AnalyzeData;
import com.bowerzlabs.service.CrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@AdminController
@Controller
@RequestMapping("/admin")
public class KraftController {
    private static final Logger log = LoggerFactory.getLogger(KraftController.class);
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CrudService crudService;

    public KraftController(ApplicationEventPublisher applicationEventPublisher, CrudService crudService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.crudService = crudService;
    }


    // render dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) throws Exception {
        model.addAttribute("components", AnalyzeData.getAnalyticsComponents());
        return "kraft-dashboard";
    }

    @GetMapping("/test")
    public String test(){
        System.out.println("/admin/test hit!");
        return "test";
    }

//    @ExceptionHandler(Exception.class)
//    public ModelAndView handleException(Exception ex) {
//        return new ModelAndView("error").addObject("exception", ex);
//    }


}
