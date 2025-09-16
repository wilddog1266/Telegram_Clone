package com.example.telegram.repository;

import com.example.telegram.entity.Chat;
import com.example.telegram.entity.ChatParticipant;
import com.example.telegram.entity.User;
import com.example.telegram.entity.enums.ParticipantRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    Optional<ChatParticipant> findByChatAndUser(Chat chat, User user);
    List<ChatParticipant> findByChat(Chat chat);
    List<ChatParticipant> findByUser(User user);
    Boolean existsByChatAndUser(Chat chat, User user);
    Integer countByChat(Chat chat);

    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chat.id = :chatId AND cp.role = :role")
    List<ChatParticipant> findByChatIdAndRole(@Param("chatId") Long chatId, @Param("role") ParticipantRole role);

    @Query("SELECT cp.user FROM ChatParticipant cp WHERE cp.chat.id = :chatId")
    List<User> findUsersByChatId(@Param("chatId") Long chatId);
}
