package com.ims.ims.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ReportsController {

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("currentPage", "/reports");
        return "reports"; // Name of your Thymeleaf template
    }
}
