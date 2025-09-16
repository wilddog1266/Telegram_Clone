package com.example.telegram.controller;

import com.example.telegram.dto.message.MessageDto;
import com.example.telegram.dto.message.SendMessageRequest;
import com.example.telegram.entity.User;
import com.example.telegram.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat/{chatId}/sendMessage")
    public void sendMessage(
            @DestinationVariable Long chatId,
            @Payload SendMessageRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        try {
            MessageDto messageDto = messageService.mapToDto(
                    messageService.sendMessage(request, chatId, user)
            );

            // Отправляем сообщение всем подписанным на чат
            messagingTemplate.convertAndSend("/topic/chat/" + chatId, messageDto);

        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            // Отправляем ошибку конкретному пользователю
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/errors",
                    "Failed to send message: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/chat/{chatId}/typing")
    public void handleTyping(
            @DestinationVariable Long chatId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        // Уведомляем других участников чата о наборе текста
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId + "/typing",
                user.getUsername() + " is typing..."
        );
    }
}