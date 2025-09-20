package com.example.telegram.service;

import com.example.telegram.dto.chat.ChatDto;
import com.example.telegram.dto.chat.CreateChatRequest;
import com.example.telegram.entity.Chat;
import com.example.telegram.entity.ChatParticipant;
import com.example.telegram.entity.User;
import com.example.telegram.entity.enums.ChatType;
import com.example.telegram.entity.enums.ParticipantRole;
import com.example.telegram.entity.enums.UserStatus;
import com.example.telegram.repository.ChatParticipantRepository;
import com.example.telegram.repository.ChatRepository;
import com.example.telegram.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserService userService;

    @Transactional
    public Chat createChat(CreateChatRequest request, User creator) {
        System.out.println("Создание чата для пользователя: " + creator.getUsername());
        System.out.println("User Id: " + creator.getId());

        Chat chat = new Chat();
        chat.setName(request.getName());
        chat.setDescription(request.getDescription());
        chat.setType(request.getType());
        chat.setCreatedBy(creator);

        System.out.println("Chat createdBy: " + chat.getCreatedBy());

        Chat savedChat = chatRepository.save(chat);

        System.out.println("Saved chat ID: " + savedChat.getId());
        System.out.println("Saved chat createdBy: " + savedChat.getCreatedBy().getId());

        addParticipant(savedChat, creator, ParticipantRole.CREATOR);

        if (request.getParticipantIds() != null) {
            for (Long participantId : request.getParticipantIds()) {
                User participant = userRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + participantId));
                addParticipant(savedChat, participant, ParticipantRole.MEMBER);
            }
        }

        return savedChat;
    }

    @Transactional
    public void addParticipants(Chat chat, List<Long> participantIds) {
        if (chat.getType() == ChatType.PRIVATE) {
            throw new IllegalArgumentException("Нельзя добавлять участников в приватный чат");
        }

        Set<Long> existingUserIds = chatParticipantRepository.findUserIdsByChat(chat);

        List<ChatParticipant> newParticipants = participantIds.stream()
                .filter(userId -> !existingUserIds.contains(userId))
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

                    ChatParticipant participant = new ChatParticipant();
                    participant.setUser(user);
                    participant.setChat(chat);
                    participant.setRole(ParticipantRole.MEMBER);
                    return participant;
                })
                .collect(Collectors.toList());

        chatParticipantRepository.saveAll(newParticipants);
    }

    @Transactional
    public Chat createChat(String name, ChatType type, List<Long> participantIds, User creator) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setType(type);

        // Добавляем создателя
        ChatParticipant creatorParticipant = new ChatParticipant();
        creatorParticipant.setUser(creator);
        creatorParticipant.setChat(chat);
        chat.getParticipants().add(creatorParticipant);

        if (type == ChatType.GROUP && participantIds != null) {
            List<User> users = userRepository.findAllById(participantIds);
            for (User u : users) {
                ChatParticipant p = new ChatParticipant();
                p.setUser(u);
                p.setChat(chat);
                chat.getParticipants().add(p);
            }
        }

        if (type == ChatType.PRIVATE) {
            if (participantIds == null || participantIds.size() != 1) {
                throw new IllegalArgumentException("Приватный чат должен иметь ровно одного участника");
            }

            User other = userRepository.findById(participantIds.get(0))
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

            ChatParticipant otherParticipant = new ChatParticipant();
            otherParticipant.setUser(other);
            otherParticipant.setChat(chat);
            chat.getParticipants().add(otherParticipant);
        }

        return chatRepository.save(chat);
    }


    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Не удалось найти чат с ID: " + chatId));
    }

    private void addParticipant(Chat chat, User user, ParticipantRole role) {
        ChatParticipant participant = new ChatParticipant();
        participant.setChat(chat);
        participant.setUser(user);
        participant.setRole(role);
        chatParticipantRepository.save(participant);
    }

    public ChatDto mapToDto(Chat chat) {
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

    public List<ChatDto> getUserChats(Long userId) {
        List<Chat> chats = chatRepository.findChatsByUserId(userId);
        return chats.stream()
                .map(this::mapToDto)
                .toList();
    }

    public Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with id: " + chatId));
    }

    public ChatDto getChatDtoById(Long chatId) {
        return mapToDto(getChatById(chatId));
    }

    public boolean isUserInChat(Long userId, Long chatId) {
        return chatParticipantRepository.existsByChatAndUser(
                getChatById(chatId),
                userRepository.findById(userId).orElseThrow()
        );
    }
}
