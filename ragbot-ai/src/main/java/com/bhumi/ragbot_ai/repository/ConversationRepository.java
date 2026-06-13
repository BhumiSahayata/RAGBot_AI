package com.bhumi.ragbot_ai.repository;

import com.bhumi.ragbot_ai.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationRepository
        extends JpaRepository<Conversation, Long> {

    void deleteById(Long id);
    List<Conversation> findByUserId(Long userId);

}