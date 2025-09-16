package com.example.telegram.dto.chat;

import com.example.telegram.dto.user.UserDto;
import com.example.telegram.entity.enums.ChatType;
import lombok.Data;

@Data
public class ChatDto {

    private Long id;
    private String name;
    private String description;
    private String avatarUrl;
    private ChatType type;
    private UserDto createdBy;
    private Integer participantsCount;
    private Long lastMessageId;
}
