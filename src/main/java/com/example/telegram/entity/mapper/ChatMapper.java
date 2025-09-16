package com.example.telegram.entity.mapper;

import com.example.telegram.dto.chat.ChatDto;
import com.example.telegram.entity.Chat;
import com.example.telegram.repository.ChatParticipantRepository;
import com.example.telegram.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatMapper {

    private final UserService userService;
    private final ChatParticipantRepository chatParticipantRepository;

    public ChatDto toDto(Chat chat) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setName(chat.getName());
        dto.setDescription(chat.getDescription());
        dto.setAvatarUrl(chat.getAvatarUrl());
        dto.setType(chat.getType());
        dto.setCreatedBy(userService.mapToDto(chat.getCreatedBy()));
        dto.setParticipantsCount(chatParticipantRepository.countByChat(chat));
        return dto;
    }
}
