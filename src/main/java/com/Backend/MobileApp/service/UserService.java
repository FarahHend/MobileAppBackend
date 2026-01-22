package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserProfileResponse getCurrentUserProfile();

    UserProfileResponse updateCurrentUserProfile(
            String username,
            String phone,
            String dateOfBirth,
            MultipartFile image
    );

    // New methods
    List<UserProfileResponse> getAllUsers();

    void deleteUser(Long userId);  // delete by ID
}
