package com.bhumi.ragbot_ai.dto;

public class ConversationDto {

    private Long id;
    private String title;

    public ConversationDto() {
    }

    public ConversationDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}