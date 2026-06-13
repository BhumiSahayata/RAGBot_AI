package com.bhumi.ragbot_ai.service;

import com.bhumi.ragbot_ai.entity.Conversation;
import com.bhumi.ragbot_ai.entity.Message;
import com.bhumi.ragbot_ai.repository.ConversationRepository;
import com.bhumi.ragbot_ai.repository.MessageRepository;
import com.bhumi.ragbot_ai.dto.ConversationDto;
import com.bhumi.ragbot_ai.dto.MessageDto;
import com.bhumi.ragbot_ai.repository.UserRepository;
import com.bhumi.ragbot_ai.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // ← Returns conversationId so frontend can track current chat
    public Long saveChat(
            String question,
            String answer,
            String email,
            Long conversationId) {

        User user = userRepository.findByEmail(email).orElseThrow();

        Conversation conversation;

        // If conversationId sent, reuse it — else create new
        if (conversationId != null) {
            conversation = conversationRepository
                    .findById(conversationId)
                    .orElseThrow();
        } else {
            conversation = new Conversation();
            conversation.setUser(user);
            conversation.setTitle(question); // first message = title
            conversation = conversationRepository.save(conversation);
        }

        Message userMessage = new Message();
        userMessage.setSender("USER");
        userMessage.setContent(question);
        userMessage.setConversation(conversation);
        messageRepository.save(userMessage);

        Message aiMessage = new Message();
        aiMessage.setSender("AI");
        aiMessage.setContent(answer);
        aiMessage.setConversation(conversation);
        messageRepository.save(aiMessage);

        return conversation.getId();
    }

    public List<ConversationDto> getAllConversations(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Conversation> conversations =
                conversationRepository.findByUserId(user.getId());
        return conversations.stream()
                .map(c -> new ConversationDto(c.getId(), c.getTitle()))
                .toList();
    }

    public List<MessageDto> getMessages(Long conversationId) {
        return messageRepository.findByConversationId(conversationId)
                .stream()
                .map(m -> new MessageDto(m.getSender(), m.getContent()))
                .toList();
    }

    public void renameConversation(Long conversationId, String newTitle) {
        Conversation conversation = conversationRepository
                .findById(conversationId).orElseThrow();
        conversation.setTitle(newTitle);
        conversationRepository.save(conversation);
    }

    public void deleteConversation(Long id) {
        conversationRepository.deleteById(id);
    }
}