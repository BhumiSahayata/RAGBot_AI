package com.bhumi.ragbot_ai.dto;

public class ChatRequest {

    private String question;
    private Long conversationId;
    private String imageBase64;  // ← ADD THIS
    private String imageMediaType; // ← ADD THIS (e.g. "image/jpeg")

    public ChatRequest() {}

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getImageMediaType() { return imageMediaType; }
    public void setImageMediaType(String imageMediaType) { this.imageMediaType = imageMediaType; }
}