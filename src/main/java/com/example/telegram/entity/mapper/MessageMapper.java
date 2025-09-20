package com.example.telegram.entity.mapper;

import com.example.telegram.dto.message.MessageDto;
import com.example.telegram.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDto toDto(Message message) {
        if (message == null) {
            return null;
        }

        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setType(message.getType());
        dto.setFileUrl(message.getFileUrl());

        if (message.getChat() != null) {
            dto.setChatId(message.getChat().getId());
        }


        return dto;
    }

    public Message toEntity(MessageDto dto) {
        if (dto == null) {
            return null;
        }

        Message message = new Message();
        message.setId(dto.getId());
        message.setContent(dto.getContent());
        message.setTimestamp(dto.getTimestamp());
        message.setType(dto.getType());
        message.setFileUrl(dto.getFileUrl());

        return message;
    }
}