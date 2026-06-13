package com.bhumi.ragbot_ai.controller;

import com.bhumi.ragbot_ai.dto.ChatRequest;
import com.bhumi.ragbot_ai.dto.ChatResponse;
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
    public ChatResponse chat(
            @RequestBody ChatRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        String answer;

        if (request.getImageBase64() != null
                && !request.getImageBase64().isEmpty()) {
            // Image analysis — skip RAG, send directly to vision model
            answer = openRouterService.askAIWithImage(
                    request.getQuestion(),
                    request.getImageBase64(),
                    request.getImageMediaType()
            );
        } else {
            // Normal RAG flow
            String ragPrompt = ragService.buildRagPrompt(
                    request.getQuestion(), email);
            answer = openRouterService.askAI(ragPrompt);
        }

        Long conversationId = chatService.saveChat(
                request.getQuestion(),
                answer,
                email,
                request.getConversationId()
        );

        return new ChatResponse(answer, conversationId);
    }

    @GetMapping("/conversations")
    public List<ConversationDto> getConversations(
            Authentication authentication) {
        return chatService.getAllConversations(
                authentication.getName());
    }

    @GetMapping("/messages/{conversationId}")
    public List<MessageDto> getMessages(
            @PathVariable Long conversationId) {
        return chatService.getMessages(conversationId);
    }

    @PutMapping("/conversation/{id}/rename")
    public void renameConversation(
            @PathVariable Long id,
            @RequestBody RenameConversationRequest request) {
        chatService.renameConversation(
                id, request.getTitle());
    }

    @DeleteMapping("/conversation/{id}")
    public void deleteConversation(
            @PathVariable Long id) {
        chatService.deleteConversation(id);
    }
}