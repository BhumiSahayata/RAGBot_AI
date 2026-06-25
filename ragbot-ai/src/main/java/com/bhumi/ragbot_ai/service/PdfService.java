package com.bhumi.ragbot_ai.service;

import com.bhumi.ragbot_ai.entity.DocumentChunk;
import com.bhumi.ragbot_ai.entity.User;
import com.bhumi.ragbot_ai.repository.DocumentChunkRepository;
import com.bhumi.ragbot_ai.repository.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    private final DocumentChunkRepository chunkRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;

    public PdfService(
            DocumentChunkRepository chunkRepository,
            UserRepository userRepository,
            EmbeddingService embeddingService) {
        this.chunkRepository = chunkRepository;
        this.userRepository = userRepository;
        this.embeddingService = embeddingService;
    }

    public String processPdf(
            MultipartFile file,
            String email) {

        try {

            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow();

            // Step 1 — Extract text from PDF
            PDDocument document =
                    org.apache.pdfbox.Loader.loadPDF(
                            file.getBytes());
            PDFTextStripper stripper =
                    new PDFTextStripper();

            String fullText =
                    stripper.getText(document);

            document.close();

            // Step 2 — Chunk the text (500 chars, 50 overlap)
            List<String> chunks =
                    chunkText(fullText, 2000, 200);

            // Step 3 — Embed and save each chunk
            for (String chunkContent : chunks) {

                String embedding =
                        embeddingService
                                .getEmbedding(chunkContent);

                if (embedding == null) continue;

                DocumentChunk chunk =
                        new DocumentChunk();

                chunk.setContent(chunkContent);
                chunk.setEmbedding(embedding);
                chunk.setFileName(
                        file.getOriginalFilename());
                chunk.setUser(user);

                chunkRepository.save(chunk);
            }

            return "PDF processed successfully. "
                    + chunks.size()
                    + " chunks stored.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to process PDF: "
                    + e.getMessage();
        }
    }

    // Splits text into overlapping chunks
    private List<String> chunkText(
            String text,
            int chunkSize,
            int overlap) {

        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + chunkSize,
                    text.length()
            );

            chunks.add(
                    text.substring(start, end)
                            .trim()
            );

            start += chunkSize - overlap;
        }

        return chunks;
    }

    public List<String> getUploadedFileNames(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        return chunkRepository
                .findByUserId(user.getId())
                .stream()
                .map(DocumentChunk::getFileName)
                .distinct()
                .toList();
    }
}