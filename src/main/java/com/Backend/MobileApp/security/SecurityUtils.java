package com.Backend.MobileApp.security;

import com.Backend.MobileApp.model.User;
import com.Backend.MobileApp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private static UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        SecurityUtils.userRepository = userRepository;
    }

    public static User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        // ✅ JWT / normal login
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }

        // ✅ Google OAuth2 login
        if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");

            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        throw new RuntimeException("Unsupported authentication type");
    }
}
