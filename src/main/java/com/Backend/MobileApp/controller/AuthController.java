package com.Backend.MobileApp.controller;

import com.Backend.MobileApp.model.*;
import com.Backend.MobileApp.repository.UserRepository;
import com.Backend.MobileApp.security.JwtUtil;
import com.Backend.MobileApp.service.AuthService;
import org.springframework.web.bind.annotation.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public AuthResponse signUp(@RequestBody SignUpRequest request) {
        User user = new User();
        user.setUsername(request.username);
        user.setEmail(request.email);
        user.setPassword(request.password);
        user.setPhone(request.phone);
        user.setNationalId(request.nationalId);
        user.setDateOfBirth(request.dateOfBirth);
        //user.setJoinDate(request.joinDate != null ? request.joinDate : LocalDate.now());
        user.setRole(Role.EMPLOYEE); // default role

        String token = authService.signUp(user);
        return new AuthResponse(token);
    }


    @PostMapping("/signin")
    public AuthResponse signIn(@RequestBody SignInRequest request) {
        String token = authService.signIn(request.email, request.password);
        return new AuthResponse(token);
    }


    @GetMapping("/google-login")
    public AuthResponse loginWithGoogle(@RequestParam String idToken) throws Exception {
        // 1️⃣ Verify ID token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("GOOGLE_CLIENT_ID")) // replace with your OAuth client ID
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new RuntimeException("Invalid ID token");
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        // 2️⃣ Find or create user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setRole(Role.EMPLOYEE);
                    newUser.setJoinDate(java.time.LocalDate.now());
                    return userRepository.save(newUser);
                });

        // 3️⃣ Generate backend JWT token
        String token = jwtUtil.generateTokenFromEmail(email);

        return new AuthResponse(token);
    }

    @PostMapping("/google")
    public AuthResponse loginWithGoogle(@RequestBody GoogleTokenRequest request) throws Exception {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(
                        "GOOGLE_CLIENT_ID"
                ))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(request.getIdToken());

        if (googleIdToken == null) {
            throw new RuntimeException("Invalid Google ID token");
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setRole(Role.EMPLOYEE);
                    newUser.setProvider(AuthProvider.GOOGLE);
                    newUser.setEnabled(true);
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateTokenFromEmail(user.getEmail());

        return new AuthResponse(token);
    }

}
