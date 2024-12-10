package com.ims.ims.Controllers;

import com.ims.ims.Entities.CustomUserDetails;
import com.ims.ims.Entities.User;
import com.ims.ims.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class UserProfileController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {

           // Fetch the only user in the database
           User user = userRepository.findTopByOrderById()
           .orElseThrow(() -> new IllegalArgumentException("User not found"));
            model.addAttribute("currentPage", "/profile");
            model.addAttribute("firstName", user.getFirstName());
            model.addAttribute("lastName", user.getLastName());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());         

        return "profile"; 
    }

    @PostMapping("/profile/edit")
    public String editProfile(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {
    
        try {
            // Fetch the only user in the database
            User user = userRepository.findTopByOrderById()
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
            // Update the user details
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
    
            // Save the changes to the database
            userRepository.save(user);
    
            // Add success message
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
    
        } catch (Exception e) {
            // Handle any errors and add an error message
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }
    
        return "redirect:/profile"; // Redirect back to the profile page
    }
    

}
