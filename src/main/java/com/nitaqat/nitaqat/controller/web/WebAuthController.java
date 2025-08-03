package com.nitaqat.nitaqat.controller.web;

import com.nitaqat.nitaqat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;


@Controller
public class WebAuthController {
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/")
    public String showLoginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            // User is already logged in → redirect to dashboard
            return "redirect:/dashboard";
        }

        return "auth/login"; // Show login page if not authenticated
    }

    @GetMapping("/dashboard")
    public String showDashboardPage(Model model, Authentication authentication) {
        String fullName = "Unknown";

        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {
            // Google login
            fullName = oauthUser.getAttribute("name");
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            // Default email/password login
            fullName = userDetails.getUsername(); // or a custom name if stored in UserDetails
        }

        model.addAttribute("adminName", fullName); // Now shows "Ahmed M.Abdelalim"
        model.addAttribute("inactiveUsers", userRepository.findByRoleAndActive("user" , false));
        model.addAttribute("activeUsers", userRepository.findByRoleAndActive("user" , true));


        return "home/dashboard"; // or your actual secured page
    }
}
