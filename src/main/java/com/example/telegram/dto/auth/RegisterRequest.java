package com.example.telegram.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {

    private String username;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;
}
