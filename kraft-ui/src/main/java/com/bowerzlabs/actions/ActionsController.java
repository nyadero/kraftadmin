package com.bowerzlabs.actions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/actions")
public class ActionsController {

    // render actions page
    @GetMapping
    public String renderActions(
            Model model
    ){
        return "actions/index";
    }
}
