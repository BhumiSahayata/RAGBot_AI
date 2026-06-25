package com.bhumi.ragbot_ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenRouterService {

    private final RestClient restClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public OpenRouterService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String askAI(String question) {
        return callGroq(question, null, null);
    }

    public String askAIWithImage(
            String question,
            String imageBase64,
            String mediaType) {
        return callGroq(question, imageBase64, mediaType);
    }

    private String callGroq(
            String question,
            String imageBase64,
            String mediaType) {

        System.out.println("===== GROQ DEBUG =====");
        System.out.println("API Key null? " + (apiKey == null));
        System.out.println("API Key starts with: " +
                (apiKey == null ? "NULL" : apiKey.substring(0, Math.min(6, apiKey.length()))));
        System.out.println("Question: " + question);

        try {
            String url = "https://api.groq.com/openai/v1/chat/completions";

            Object messageContent;

            if (imageBase64 != null && !imageBase64.isEmpty()) {
                // Vision request
                messageContent = List.of(
                        Map.of("type", "text", "text", question),
                        Map.of("type", "image_url",
                                "image_url", Map.of(
                                        "url", "data:" + mediaType
                                                + ";base64," + imageBase64
                                )
                        )
                );
            } else {
                messageContent = question;
            }

            // Use vision model for images, fast model for text
            String model = (imageBase64 != null && !imageBase64.isEmpty())
                    ? "meta-llama/llama-4-scout-17b-16e-instruct"
                    : "llama-3.1-8b-instant";

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "user", "content", messageContent)
                    )
            );

            Map response = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            List choices = (List) response.get("choices");
            Map choice = (Map) choices.get(0);
            Map message = (Map) choice.get("message");
            return message.get("content").toString();

        } catch (Exception e) {
            System.out.println("===== GROQ ERROR =====");
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}