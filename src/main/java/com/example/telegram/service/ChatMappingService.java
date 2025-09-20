package com.example.telegram.service;

import com.example.telegram.dto.chat.ChatDto;
import com.example.telegram.entity.Chat;
import com.example.telegram.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMappingService {

    private final UserService userService;
    private final ChatParticipantRepository chatParticipantRepository;

    public ChatDto mapToFullDto(Chat chat) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setName(chat.getName());
        dto.setDescription(chat.getDescription());
        dto.setAvatarUrl(chat.getAvatarUrl());
        dto.setType(chat.getType());

        if (chat.getCreatedBy() != null) {
            dto.setCreatedBy(userService.mapToDto(chat.getCreatedBy()));
        }

        dto.setParticipantsCount(chatParticipantRepository.countByChat(chat));

        return dto;
    }
}