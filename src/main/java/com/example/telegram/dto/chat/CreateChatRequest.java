package com.example.telegram.dto.chat;

import com.example.telegram.entity.enums.ChatType;
import lombok.Data;

@Data
public class CreateChatRequest {
    private String name;
    private String description;
    private ChatType type;
    private Long[] participantIds;
}
