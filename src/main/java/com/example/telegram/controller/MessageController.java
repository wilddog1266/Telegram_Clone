package com.example.telegram.controller;

import com.example.telegram.dto.message.MessageDto;
import com.example.telegram.dto.message.SendMessageRequest;
import com.example.telegram.entity.Chat;
import com.example.telegram.entity.Message;
import com.example.telegram.entity.User;
import com.example.telegram.entity.mapper.MessageMapper;
import com.example.telegram.repository.ChatRepository;
import com.example.telegram.repository.MessageRepository;
import com.example.telegram.repository.UserRepository;
import com.example.telegram.service.MessageService;
import com.example.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            Authentication authentication,
            @PathVariable Long chatId,
            @RequestBody SendMessageRequest request
    ) {
        String username = authentication.getName();
        User currentUser = userService.getByUsername(username);

        return ResponseEntity.ok(
                messageService.mapToDto(
                        messageService.sendMessage(request, chatId, currentUser)
                )
        );
    }

    @PostMapping("/api/v1/chats/{chatId}/messages")
    public MessageDto sendMessage(
            @PathVariable Long chatId,
            @RequestBody MessageDto dto,
            Principal principal
    ) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow();
        Chat chat = chatRepository.findById(chatId).orElseThrow();

        if (!chat.getUsers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not in this chat");
        }

        Message msg = new Message();
        msg.setContent(dto.getContent());
        msg.setChat(chat);
        msg.setSender(user);
        msg.setTimestamp(Instant.now());

        return messageMapper.toDto(messageRepository.save(msg));
    }

    @GetMapping
    public ResponseEntity<Page<MessageDto>> getChatMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(messageService.getChatMessages(chatId, pageable));
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<MessageDto> editMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long chatId,
            @PathVariable Long messageId,
            @RequestParam String content
    ) {
        return ResponseEntity.ok(
                messageService.mapToDto(
                        messageService.editMessage(messageId, content, currentUser)
                )
        );
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long chatId,
            @PathVariable Long messageId
    ) {
        messageService.deleteMessage(messageId, currentUser);
        return ResponseEntity.ok().build();
    }
}