package com.example.telegram.repository;

import com.example.telegram.entity.Chat;
import com.example.telegram.entity.User;
import com.example.telegram.entity.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

//    List<Chat> findByType(ChatType type);
//    List<Chat> findByCreatedBy(User createdBy);

    @Query("SELECT c FROM Chat c JOIN c.participants cp WHERE cp.user.id = :userId")
    List<Chat> findChatsByUserId(@Param("userId") Long userId);

//    @Query("SELECT c FROM Chat c JOIN c.participants cp WHERE cp.user.id = :userId AND c.type = com.example.telegram.entity.enums.ChatType.PRIVATE")
//    List<Chat> findPrivateChatsByUserId(@Param("userId") Long userId);

    Optional<Chat> findByIdAndParticipants_User_Id(Long chatId, Long userId);
}
