package com.Backend.MobileApp.model;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role,
        java.time.LocalDate dateOfBirth) {}

