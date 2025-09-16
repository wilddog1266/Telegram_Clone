package com.example.telegram.dto.user;

import com.example.telegram.entity.enums.UserStatus;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
    private UserStatus status;
}
