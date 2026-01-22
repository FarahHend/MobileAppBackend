package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.User;
import com.Backend.MobileApp.repository.UserRepository;
import com.Backend.MobileApp.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String signUp(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new RuntimeException("Username already exists");
        if (userRepository.existsByEmail(user.getEmail()))
            throw new RuntimeException("Email already exists");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return jwtUtil.generateToken(user.getUsername());
    }

    public String signIn(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid password");

        return jwtUtil.generateToken(user.getEmail()); // generate token using email
    }

}
