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

    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;
    private final UserRepository userRepository;

    public RagService(
            DocumentChunkRepository chunkRepository,
            EmbeddingService embeddingService,
            UserRepository userRepository) {
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
        this.userRepository = userRepository;
    }

    public String buildRagPrompt(String question, String email) {

        try {

            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow();

            List<DocumentChunk> allChunks =
                    chunkRepository.findByUserId(user.getId());

            System.out.println("\n========================================");
            System.out.println("RAG DEBUG START");
            System.out.println("========================================");

            System.out.println("QUESTION = " + question);
            System.out.println("TOTAL CHUNKS FOUND = " + allChunks.size());

            // No PDF uploaded
            if (allChunks.isEmpty()) {
                System.out.println("NO CHUNKS FOUND");
                return question;
            }

            String questionEmbedding =
                    embeddingService.getEmbedding(question);

            if (questionEmbedding == null) {
                System.out.println("QUESTION EMBEDDING FAILED");
                return question;
            }

            double[] questionVector =
                    parseEmbedding(questionEmbedding);

            List<DocumentChunk> topChunks = allChunks.stream()
                    .sorted(Comparator.comparingDouble(chunk ->
                            -cosineSimilarity(
                                    questionVector,
                                    parseEmbedding(chunk.getEmbedding())
                            )
                    ))
                    .limit(5)
                    .toList();

            System.out.println("TOP CHUNKS SELECTED = "
                    + topChunks.size());

            StringBuilder context = new StringBuilder();

            int chunkNo = 1;

            for (DocumentChunk chunk : topChunks) {

                System.out.println("\n----------- CHUNK "
                        + chunkNo + " -----------");

                System.out.println(chunk.getContent());

                System.out.println("------------------------------");

                context.append(chunk.getContent());
                context.append("\n---\n");

                chunkNo++;
            }

            String finalPrompt = """
                You are a helpful AI assistant.
                Use the following context from the user's documents to answer the question.
                If the answer is not in the context, answer from your own knowledge.

                CONTEXT:
                """ + context + """

                USER QUESTION:
                """ + question;

            System.out.println("\n========================================");
            System.out.println("PROMPT LENGTH = "
                    + finalPrompt.length());
            System.out.println("========================================");

            System.out.println(finalPrompt);

            System.out.println("\n========================================");
            System.out.println("RAG DEBUG END");
            System.out.println("========================================\n");

            return finalPrompt;

        } catch (Exception e) {
            e.printStackTrace();
            return question;
        }
    }

    // Parse "[0.1,0.2,...]" string to double array
    private double[] parseEmbedding(String embedding) {
        String cleaned = embedding
                .replace("[", "")
                .replace("]", "")
                .trim();
        String[] parts = cleaned.split(",");
        double[] vector = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Double.parseDouble(parts[i].trim());
        }
        return vector;
    }

    // Cosine similarity between two vectors
    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, normA = 0, normB = 0;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}