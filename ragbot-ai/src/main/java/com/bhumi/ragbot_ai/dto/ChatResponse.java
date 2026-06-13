package com.bhumi.ragbot_ai.dto;

public class ChatResponse {

    private String answer;
    private Long conversationId; // ← ADD THIS

    public ChatResponse() {}

    public ChatResponse(String answer, Long conversationId) {
        this.answer = answer;
        this.conversationId = conversationId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}