package com.example.telegram.dto.user;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
}
