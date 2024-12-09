package com.ims.ims.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        // Return the name of your login HTML template
        return "login"; // Make sure you have a 'login.html' in your templates folder
    }
}
