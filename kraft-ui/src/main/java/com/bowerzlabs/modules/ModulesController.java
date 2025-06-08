package com.bowerzlabs.modules;

import com.bowerzlabs.annotations.AdminController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/modules")
//@AdminController
public class ModulesController {
    // render modules page
    @GetMapping
    public String modules(){
        return "modules/index";
    }

    // activate module
public void hello(){
    }

    // deactivate module

}


