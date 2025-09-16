package com.example.telegram.repository;

import com.example.telegram.entity.Chat;
import com.example.telegram.entity.Message;
import com.example.telegram.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByChatOrderByTimestampDesc(Chat chat, Pageable pageable);
    List<Message> findByChatAndTimestampAfter(Chat chat, Instant timestamp);
    List<Message> findByChatId(Long chatId);
    Long countByChat(Chat chat);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.isDeleted = false ORDER BY m.timestamp DESC")
    Page<Message> findMessagesByChatId(@Param("chatId") Long chatId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.sender.id = :userId AND m.isDeleted = false")
    Page<Message> findMessagesByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.timestamp < :beforeTimestamp ORDER BY m.timestamp DESC")
    Page<Message> findMessagesBeforeTimestamp(@Param("chatId") Long chatId, @Param("beforeTimestamp") Instant beforeTimestamp, Pageable pageable);
}