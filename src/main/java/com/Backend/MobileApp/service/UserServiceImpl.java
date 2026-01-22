package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.DepartmentName;
import com.Backend.MobileApp.model.User;
import com.Backend.MobileApp.model.UserProfileResponse;
import com.Backend.MobileApp.repository.UserRepository;
import com.Backend.MobileApp.security.SecurityUtils;
import com.Backend.MobileApp.service.FileStorageService;
import com.Backend.MobileApp.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public UserServiceImpl(UserRepository userRepository,
                           FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        User user = SecurityUtils.getCurrentUser();
        user = userRepository.findWithDepartmentsById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public UserProfileResponse updateCurrentUserProfile(String username,
                                                        String phone,
                                                        String dateOfBirth,
                                                        MultipartFile image) {
        User authUser = SecurityUtils.getCurrentUser();
        User user = userRepository.findWithDepartmentsById(authUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (username != null && !username.isEmpty()) user.setUsername(username);
        if (phone != null && !phone.isEmpty()) user.setPhone(phone);

        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            LocalDate dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_DATE);
            user.setDateOfBirth(dob);
        }

        if (image != null && !image.isEmpty()) {
            try {
                user.setImage(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read image file", e);
            }
        }

        userRepository.save(user);
        return mapToResponse(user);
    }

    // --- New methods ---

    @Override
    public List<UserProfileResponse> getAllUsers() {
        // Fetch all users (no need for join with Department table anymore)
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToResponse) // map each user to UserProfileResponse
                .collect(Collectors.toList());
    }


    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // --- Helper method ---
    private UserProfileResponse mapToResponse(User user) {
        // Wrap the single department into a Set
        Set<DepartmentName> departments = user.getDepartment() != null
                ? Set.of(user.getDepartment())
                : Set.of(); // empty set if null

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getNationalId(),
                user.getImage(),
                user.getDateOfBirth(),
                departments,
                user.getRole().name()
        );
    }

}

