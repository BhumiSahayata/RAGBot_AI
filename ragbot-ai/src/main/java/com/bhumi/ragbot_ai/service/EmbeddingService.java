package com.bhumi.ragbot_ai.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class EmbeddingService {

    // No external API needed — we use TF-IDF style keyword embeddings
    // This works fully offline and is fast
    public String getEmbedding(String text) {

        try {

            String[] words = text
                    .toLowerCase()
                    .replaceAll("[^a-z0-9 ]", "")
                    .split("\\s+");

            // Fixed vocabulary size = 384 dimensions
            double[] vector = new double[384];

            for (String word : words) {
                if (word.isEmpty()) continue;

                // Hash word into vector position
                int hash = Math.abs(word.hashCode()) % 384;
                vector[hash] += 1.0;

                // Add bigram features for better matching
                for (int i = 0; i < word.length() - 1; i++) {
                    String bigram = word.substring(i, i + 2);
                    int bigramHash =
                            Math.abs(bigram.hashCode()) % 384;
                    vector[bigramHash] += 0.5;
                }
            }

            // Normalize the vector
            double norm = 0;
            for (double v : vector) norm += v * v;
            norm = Math.sqrt(norm);

            if (norm > 0) {
                for (int i = 0; i < vector.length; i++) {
                    vector[i] /= norm;
                }
            }

            // Convert to "[0.1,0.2,...]" string
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < vector.length; i++) {
                sb.append(vector[i]);
                if (i < vector.length - 1) sb.append(",");
            }
            sb.append("]");

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}