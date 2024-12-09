package com.ims.ims.Controllers;

import com.ims.ims.Entities.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserProfileController {

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {

            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("currentPage", "/profile");
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("email", userDetails.getEmail());
            model.addAttribute("roles", userDetails.getAuthorities());
         

        return "profile"; 
    }
}
