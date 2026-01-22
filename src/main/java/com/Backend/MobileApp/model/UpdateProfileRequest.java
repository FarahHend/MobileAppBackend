package com.Backend.MobileApp.model;

import java.time.LocalDate;

public record UpdateProfileRequest(
        String username,
        String phone,
        String image,
        LocalDate dateOfBirth
) {

}
