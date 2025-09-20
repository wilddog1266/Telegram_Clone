package com.example.telegram.controller;

import com.example.telegram.dto.chat.ChatDto;
import com.example.telegram.dto.user.UpdateUserRequest;
import com.example.telegram.dto.user.UserDto;
import com.example.telegram.entity.User;
import com.example.telegram.entity.mapper.ChatMapper;
import com.example.telegram.repository.UserRepository;
import com.example.telegram.service.ChatMappingService;
import com.example.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ChatMappingService chatMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        return ResponseEntity.ok(userService.getCurrentUserDto(user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        User user = userService.getById(userId);
        return ResponseEntity.ok(userService.getCurrentUserDto(user));
    }

    @GetMapping("/{userId}/chats")
    public List<ChatDto> getUserChats(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return user.getChats().stream()
                .map(chatMapper::mapToFullDto)
                .toList();
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateUser(
            Authentication authentication,
            @RequestBody UpdateUserRequest request
    ) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        return ResponseEntity.ok(userService.updateUser(user, request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }
}