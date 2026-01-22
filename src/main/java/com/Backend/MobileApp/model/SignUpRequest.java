package com.Backend.MobileApp.model;

import java.time.LocalDate;

public class SignUpRequest {
    public String username;
    public String email;
    public String password;
    public String phone;
    public String nationalId;
    public LocalDate dateOfBirth; // match your entity type
    //public LocalDate joinDate;    // optional, if you want to allow client to send it
    //public String role;           // optional, can default in code
}
