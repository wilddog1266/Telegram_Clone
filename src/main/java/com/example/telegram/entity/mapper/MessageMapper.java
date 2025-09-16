package com.example.telegram.entity.mapper;

import com.example.telegram.dto.message.MessageDto;
import com.example.telegram.entity.Message;
import com.example.telegram.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageMapper {

    private final UserService userService;

    public MessageDto toDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setChatId(message.getChat().getId());
        dto.setSender(userService.mapToDto(message.getSender()));
        dto.setTimestamp(message.getTimestamp());
        dto.setType(message.getType());
        dto.setFileUrl(message.getFileUrl());
        dto.setIsEdited(message.isEdited());

        if (message.getReplyTo() != null) {
            dto.setReplyTo(toDto(message.getReplyTo()));
        }

        return dto;
    }
}
