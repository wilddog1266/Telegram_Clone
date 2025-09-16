package com.example.telegram.controller;

import com.example.telegram.dto.chat.ChatDto;
import com.example.telegram.dto.chat.CreateChatRequest;
import com.example.telegram.dto.message.MessageDto;
import com.example.telegram.entity.Chat;
import com.example.telegram.entity.User;
import com.example.telegram.entity.mapper.MessageMapper;
import com.example.telegram.repository.ChatRepository;
import com.example.telegram.repository.MessageRepository;
import com.example.telegram.repository.UserRepository;
import com.example.telegram.service.ChatService;
import com.example.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @PostMapping
    public ResponseEntity<ChatDto> createChat(
            Authentication authentication,
            @RequestBody CreateChatRequest request
    ) {
        String username = authentication.getName();
        User currentUser = userService.getByUsername(username);
        return ResponseEntity.ok(chatService.mapToDto(chatService.createChat(request, currentUser)));
    }

    @GetMapping
    public ResponseEntity<List<ChatDto>> getUserChats(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.getByUsername(username);
        return ResponseEntity.ok(chatService.getUserChats(currentUser.getId()));
    }

    @GetMapping("/{chatId}/messages")
    public List<MessageDto> getMessages(@PathVariable Long chatId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow();
        Chat chat = chatRepository.findById(chatId).orElseThrow();

        if (!chat.getUsers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not in this chat");
        }

        return messageRepository.findByChatId(chatId).stream()
                .map(messageMapper::toDto)
                .toList();
    }

    @PutMapping("/{chatId}")
    public

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDto> getChat(
            Authentication authentication,
            @PathVariable Long chatId
    ) {
        String username = authentication.getName();
        User currentUser = userService.getByUsername(username);

        if (!chatService.isUserInChat(currentUser.getId(), chatId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(chatService.getChatDtoById(chatId));
    }
}