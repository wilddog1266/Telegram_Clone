package com.example.telegram.dto.message;

import com.example.telegram.entity.enums.MessageType;
import lombok.Data;

@Data
public class SendMessageRequest {
    private String content;
    private MessageType type;
    private String fileUrl;
    private Long replyToId;
}
