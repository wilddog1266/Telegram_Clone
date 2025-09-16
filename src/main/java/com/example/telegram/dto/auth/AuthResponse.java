package com.example.telegram.dto.auth;

import com.example.telegram.dto.user.UserDto;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UserDto user;
}
