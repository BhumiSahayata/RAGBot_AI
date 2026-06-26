package com.bhumi.ragbot_ai.dto;

public class SectionedChatResponse {

    private String psychologyAnswer;
    private String financeAnswer;
    private String spiritualityAnswer;
    private String finalAnswer;
    private Long conversationId;

    public SectionedChatResponse() {}

    public SectionedChatResponse(
            String psychologyAnswer,
            String financeAnswer,
            String spiritualityAnswer,
            String finalAnswer,
            Long conversationId) {
        this.psychologyAnswer = psychologyAnswer;
        this.financeAnswer = financeAnswer;
        this.spiritualityAnswer = spiritualityAnswer;
        this.finalAnswer = finalAnswer;
        this.conversationId = conversationId;
    }

    public String getPsychologyAnswer() { return psychologyAnswer; }
    public void setPsychologyAnswer(String psychologyAnswer) { this.psychologyAnswer = psychologyAnswer; }

    public String getFinanceAnswer() { return financeAnswer; }
    public void setFinanceAnswer(String financeAnswer) { this.financeAnswer = financeAnswer; }

    public String getSpiritualityAnswer() { return spiritualityAnswer; }
    public void setSpiritualityAnswer(String spiritualityAnswer) { this.spiritualityAnswer = spiritualityAnswer; }

    public String getFinalAnswer() { return finalAnswer; }
    public void setFinalAnswer(String finalAnswer) { this.finalAnswer = finalAnswer; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}