package com.example.telegram.service;

import com.example.telegram.dto.message.MessageDto;
import com.example.telegram.dto.message.SendMessageRequest;
import com.example.telegram.entity.Chat;
import com.example.telegram.entity.Message;
import com.example.telegram.entity.User;
import com.example.telegram.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;

    @Transactional
    public Message sendMessage(SendMessageRequest request, Long chatId, User sender) {
        Chat chat = chatService.getChatById(chatId);

        if (!chatService.isUserInChat(sender.getId(), chatId)) {
            throw new RuntimeException("User is not a member of this chat");
        }

        Message message = new Message();
        message.setContent(request.getContent());
        message.setChat(chat);
        message.setSender(sender);
        message.setType(request.getType());
        message.setFileUrl(request.getFileUrl());
        message.setTimestamp(Instant.now());

        if (request.getReplyToId() != null) {
            Message replyTo = messageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new RuntimeException("Reply message not found"));
            message.setReplyTo(replyTo);
        }

        return messageRepository.save(message);
    }

    public Page<MessageDto> getChatMessages(Long chatId, Pageable pageable) {
        Chat chat = chatService.getChatById(chatId);
        Page<Message> messages = messageRepository.findMessagesByChatId(chatId, pageable);
        return messages.map(this::mapToDto);
    }

    @Transactional
    public Message editMessage(Long messageId, String newContent, User editor) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSender().getId().equals(editor.getId())) {
            throw new RuntimeException("You can only edit your own messages");
        }

        message.setContent(newContent);
        message.setEdited(true);
        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Long messageId, User deleter) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // Проверяем права: либо отправитель, либо админ чата
        boolean isSender = message.getSender().getId().equals(deleter.getId());
        // TODO: Добавить проверку на админа чата

        if (!isSender) {
            throw new RuntimeException("You can only delete your own messages");
        }

        message.setDeleted(true);
        messageRepository.save(message);
    }

    public MessageDto mapToDto(Message message) {
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
            dto.setReplyTo(mapToDto(message.getReplyTo()));
        }

        return dto;
    }
}