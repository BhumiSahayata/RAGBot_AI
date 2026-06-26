package com.bhumi.ragbot_ai.controller;

import com.bhumi.ragbot_ai.dto.ChatRequest;
import com.bhumi.ragbot_ai.dto.SectionedChatResponse;
import com.bhumi.ragbot_ai.repository.MessageRepository;
import com.bhumi.ragbot_ai.service.ChatService;
import com.bhumi.ragbot_ai.service.OpenRouterService;
import com.bhumi.ragbot_ai.service.RagService;
import com.bhumi.ragbot_ai.dto.ConversationDto;
import com.bhumi.ragbot_ai.dto.MessageDto;
import com.bhumi.ragbot_ai.dto.RenameConversationRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenRouterService openRouterService;
    private final ChatService chatService;
    private final MessageRepository messageRepository;
    private final RagService ragService;

    public ChatController(
            OpenRouterService openRouterService,
            ChatService chatService,
            MessageRepository messageRepository,
            RagService ragService) {
        this.openRouterService = openRouterService;
        this.chatService = chatService;
        this.messageRepository = messageRepository;
        this.ragService = ragService;
    }

    @PostMapping
    public SectionedChatResponse chat(
            @RequestBody ChatRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        String psychologyAnswer;
        String financeAnswer;
        String spiritualityAnswer;
        String finalAnswer;

        if (request.getImageBase64() != null && !request.getImageBase64().isEmpty()) {
            // Image analysis — single answer, replicated across all sections
            String imageAnswer = openRouterService.askAIWithImage(
                    request.getQuestion(),
                    request.getImageBase64(),
                    request.getImageMediaType()
            );
            psychologyAnswer = imageAnswer;
            financeAnswer = imageAnswer;
            spiritualityAnswer = imageAnswer;
            finalAnswer = imageAnswer;
        } else {
            // 3-section RAG flow
            String[] answers = ragService.buildSectionedAnswers(
                    request.getQuestion(), email);
            psychologyAnswer    = answers[0];
            financeAnswer       = answers[1];
            spiritualityAnswer  = answers[2];
            finalAnswer         = answers[3];
        }

        // Save the final combined answer as the stored message
        Long conversationId = chatService.saveChat(
                request.getQuestion(),
                finalAnswer,
                email,
                request.getConversationId()
        );

        return new SectionedChatResponse(
                psychologyAnswer,
                financeAnswer,
                spiritualityAnswer,
                finalAnswer,
                conversationId
        );
    }

    @GetMapping("/conversations")
    public List<ConversationDto> getConversations(Authentication authentication) {
        return chatService.getAllConversations(authentication.getName());
    }

    @GetMapping("/messages/{conversationId}")
    public List<MessageDto> getMessages(@PathVariable Long conversationId) {
        return chatService.getMessages(conversationId);
    }

    @PutMapping("/conversation/{id}/rename")
    public void renameConversation(
            @PathVariable Long id,
            @RequestBody RenameConversationRequest request) {
        chatService.renameConversation(id, request.getTitle());
    }

    @DeleteMapping("/conversation/{id}")
    public void deleteConversation(@PathVariable Long id) {
        chatService.deleteConversation(id);
    }
}