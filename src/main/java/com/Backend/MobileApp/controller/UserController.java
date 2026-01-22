package com.Backend.MobileApp.controller;

import com.Backend.MobileApp.model.UpdateProfileRequest;
import com.Backend.MobileApp.model.User;
import com.Backend.MobileApp.model.UserProfileResponse;
import com.Backend.MobileApp.model.UserResponse;
import com.Backend.MobileApp.security.CustomUserDetails;
import com.Backend.MobileApp.repository.UserRepository;
import com.Backend.MobileApp.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            User user = ((CustomUserDetails) principal).getUser();
            return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), user.getDateOfBirth());
        } else if (principal instanceof OAuth2User) {
            OAuth2User oauthUser = (OAuth2User) principal;
            String email = oauthUser.getAttribute("email");
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), user.getDateOfBirth());
        }

        throw new RuntimeException("Unsupported authentication type");
    }

    // ✅ UPDATE profile (with image)
    @PutMapping(
            value = "/me",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserProfileResponse updateMe(
            @RequestPart(required = false) String username,
            @RequestPart(required = false) String phone,
            @RequestPart(required = false) String dateOfBirth,
            @RequestPart(required = false) MultipartFile image
    ) {
        return userService.updateCurrentUserProfile(
                username,
                phone,
                dateOfBirth,
                image
        );
    }

    // GET all users
    @GetMapping("/all")
    public List<UserProfileResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    // DELETE user by ID
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
