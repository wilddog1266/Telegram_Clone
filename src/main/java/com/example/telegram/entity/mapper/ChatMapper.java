package com.example.telegram.entity.mapper;

import com.example.telegram.dto.chat.ChatDto;
import com.example.telegram.entity.Chat;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ChatDto toDto(Chat chat) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setName(chat.getName());
        dto.setDescription(chat.getDescription());
        dto.setAvatarUrl(chat.getAvatarUrl());
        dto.setType(chat.getType());
        dto.setParticipantsCount(chat.getParticipants().size());
        return dto;
    }
}