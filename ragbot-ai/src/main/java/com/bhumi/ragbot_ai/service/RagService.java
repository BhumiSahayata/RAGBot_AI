package com.bhumi.ragbot_ai.service;

import com.bhumi.ragbot_ai.entity.DocumentChunk;
import com.bhumi.ragbot_ai.entity.User;
import com.bhumi.ragbot_ai.repository.DocumentChunkRepository;
import com.bhumi.ragbot_ai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RagService {

    private static final String PSYCHOLOGY = "PSYCHOLOGY";
    private static final String FINANCE = "FINANCE";
    private static final String SPIRITUALITY = "SPIRITUALITY";

    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;
    private final UserRepository userRepository;
    private final OpenRouterService openRouterService;

    public RagService(
            DocumentChunkRepository chunkRepository,
            EmbeddingService embeddingService,
            UserRepository userRepository,
            OpenRouterService openRouterService) {
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
        this.userRepository = userRepository;
        this.openRouterService = openRouterService;
    }

    public String[] buildSectionedAnswers(String question, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        double[] questionVector = getQuestionVector(question);

        String psychologyAnswer = answerForSection(question, questionVector, user.getId(), PSYCHOLOGY);
        String financeAnswer = answerForSection(question, questionVector, user.getId(), FINANCE);
        String spiritualityAnswer = answerForSection(question, questionVector, user.getId(), SPIRITUALITY);
        String finalAnswer = combineAnswers(question, psychologyAnswer, financeAnswer, spiritualityAnswer);

        return new String[]{psychologyAnswer, financeAnswer, spiritualityAnswer, finalAnswer};
    }

    private String answerForSection(
            String question,
            double[] questionVector,
            Long userId,
            String category) {

        try {
            List<DocumentChunk> chunks = chunkRepository.findByUserIdAndCategory(userId, category);

            String prompt;
            if (chunks.isEmpty()) {
                prompt = """
                    You are an expert in %s.
                    Answer the following question from your own expert knowledge.
                    Be clear, concise, and helpful. Do not mention other knowledge sections.

                    Question: %s
                    """.formatted(sectionLabel(category), question);
            } else {
                List<DocumentChunk> topChunks = chunks.stream()
                        .filter(chunk -> chunk.getEmbedding() != null && !chunk.getEmbedding().isBlank())
                        .sorted(Comparator.comparingDouble(chunk ->
                                -similarityToQuestion(questionVector, chunk.getEmbedding())))
                        .limit(5)
                        .toList();

                StringBuilder context = new StringBuilder();
                for (DocumentChunk chunk : topChunks) {
                    context.append(chunk.getContent()).append("\n---\n");
                }

                prompt = """
                    You are an expert in %s.
                    Use only the following %s document context to answer the question.
                    Do not use or mention documents from any other section.
                    If the context is relevant, use it. If not fully covered, supplement from your own expert knowledge.
                    Be clear, concise, and helpful.

                    --- Document Context ---
                    %s
                    --- End of Context ---

                    Question: %s
                    """.formatted(sectionLabel(category), sectionLabel(category), context, question);
            }

            return openRouterService.askAI(prompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to generate " + sectionLabel(category) + " answer at this time.";
        }
    }

    private String combineAnswers(
            String question,
            String psychologyAnswer,
            String financeAnswer,
            String spiritualityAnswer) {

        String combinePrompt = """
            You have received three expert answers to the same question, each from a different domain.
            Combine them into one comprehensive, well-structured final answer.
            Do not repeat section labels; write a cohesive unified answer.
            Be insightful, clear, and natural.

            Original Question: %s

            Human Psychology Expert Answer:
            %s

            Finance Expert Answer:
            %s

            Spirituality Expert Answer:
            %s

            Now write the final combined answer:
            """.formatted(question, psychologyAnswer, financeAnswer, spiritualityAnswer);

        try {
            return openRouterService.askAI(combinePrompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to generate combined answer at this time.";
        }
    }

    private double[] getQuestionVector(String question) {
        try {
            String embedding = embeddingService.getEmbedding(question);
            return parseEmbedding(embedding);
        } catch (Exception e) {
            return new double[384];
        }
    }

    private String sectionLabel(String category) {
        return switch (category) {
            case PSYCHOLOGY -> "Human Psychology";
            case FINANCE -> "Finance";
            case SPIRITUALITY -> "Spirituality";
            default -> category;
        };
    }

    private double[] parseEmbedding(String embedding) {
        if (embedding == null || embedding.isBlank()) {
            return new double[0];
        }

        String cleaned = embedding.replace("[", "").replace("]", "").trim();
        if (cleaned.isBlank()) {
            return new double[0];
        }

        String[] parts = cleaned.split(",");
        double[] vector = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Double.parseDouble(parts[i].trim());
        }
        return vector;
    }

    private double similarityToQuestion(double[] questionVector, String chunkEmbedding) {
        try {
            return cosineSimilarity(questionVector, parseEmbedding(chunkEmbedding));
        } catch (Exception e) {
            return 0;
        }
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0;
        double normA = 0;
        double normB = 0;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) {
            return 0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
