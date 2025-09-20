package com.example.telegram.controller;

import com.example.telegram.dto.auth.AuthResponse;
import com.example.telegram.dto.auth.LoginRequest;
import com.example.telegram.dto.auth.RegisterRequest;
import com.example.telegram.repository.UserRepository;
import com.example.telegram.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) throws BadRequestException {
        if(request.getPhoneNumber() == null
                || !request.getPhoneNumber().matches("^\\+\\d{10,15}$")) {
            throw new BadRequestException("Телефон должен быть в формате +1234567890");
        }
        if(request.getUsername() == null
                || userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("Пользователь с таким username уже существует");
        }
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}