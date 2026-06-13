package com.bhumi.ragbot_ai.repository;

import com.bhumi.ragbot_ai.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversationId(Long conversationId);
}