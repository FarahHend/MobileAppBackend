package com.Backend.MobileApp.model;

import java.time.LocalDate;
import java.util.Set;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String phone,
        String nationalId,
        byte[] image,
        LocalDate dateOfBirth,
        //Set<String> departments,
        //String provider,
        Set<DepartmentName> departments,
        String role
) {}