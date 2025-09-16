package com.example.telegram.dto.message;

import com.example.telegram.dto.user.UserDto;
import com.example.telegram.entity.enums.MessageType;
import lombok.Data;

import java.time.Instant;

@Data
public class MessageDto {
    private Long id;
    private String content;
    private Long chatId;
    private UserDto sender;
    private Instant timestamp;
    private MessageType type;
    private String fileUrl;
    private Boolean isEdited;
    private MessageDto replyTo;
}
