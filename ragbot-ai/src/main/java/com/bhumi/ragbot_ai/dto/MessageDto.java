package com.bhumi.ragbot_ai.dto;

public class MessageDto {

    private String sender;
    private String content;

    public MessageDto() {
    }

    public MessageDto(
            String sender,
            String content) {

        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}