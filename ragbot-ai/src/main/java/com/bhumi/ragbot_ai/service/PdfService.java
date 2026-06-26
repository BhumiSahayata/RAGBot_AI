package com.bhumi.ragbot_ai.service;

import com.bhumi.ragbot_ai.entity.DocumentChunk;
import com.bhumi.ragbot_ai.entity.User;
import com.bhumi.ragbot_ai.repository.DocumentChunkRepository;
import com.bhumi.ragbot_ai.repository.UserRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    public static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024;
    public static final int MAX_BOOKS_PER_SECTION = 5;

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

    public String processPdf(MultipartFile file, String email, String category) {
        try {
            String normalizedCategory = normalizeCategory(category);
            if (normalizedCategory == null) {
                return "Invalid section. Choose Human Psychology, Finance, or Spirituality.";
            }

            String originalFileName = cleanFileName(file.getOriginalFilename());
            if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
                return "Only PDF files are allowed.";
            }

            if (file.getSize() > MAX_FILE_SIZE_BYTES) {
                return "File too large. Maximum allowed size per PDF is 10 MB.";
            }

            User user = userRepository.findByEmail(email).orElseThrow();

            boolean duplicate = chunkRepository.existsByUserIdAndCategoryAndFileName(
                    user.getId(), normalizedCategory, originalFileName);
            if (duplicate) {
                return "This PDF is already uploaded in " + sectionLabel(normalizedCategory) + ".";
            }

            long existingBookCount = chunkRepository.countDistinctFilesByUserIdAndCategory(
                    user.getId(), normalizedCategory);
            if (existingBookCount >= MAX_BOOKS_PER_SECTION) {
                return "Section limit reached. You can upload a maximum of "
                        + MAX_BOOKS_PER_SECTION + " PDF books per section.";
            }

            String fullText;
            try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                fullText = stripper.getText(document);
            }

            if (fullText == null || fullText.isBlank()) {
                return "Failed to process PDF: no readable text was found.";
            }

            List<String> chunks = chunkText(fullText, 3000, 300);
            int storedChunks = 0;

            for (String chunkContent : chunks) {
                if (chunkContent.isBlank()) {
                    continue;
                }

                String embedding = embeddingService.getEmbedding(chunkContent);
                if (embedding == null || embedding.isBlank()) {
                    continue;
                }

                DocumentChunk chunk = new DocumentChunk();
                chunk.setContent(chunkContent);
                chunk.setEmbedding(embedding);
                chunk.setFileName(originalFileName);
                chunk.setCategory(normalizedCategory);
                chunk.setUser(user);

                chunkRepository.save(chunk);
                storedChunks++;
            }

            if (storedChunks == 0) {
                return "Failed to process PDF: embeddings could not be generated.";
            }

            return "PDF processed successfully. "
                    + storedChunks + " chunks stored under " + sectionLabel(normalizedCategory) + ".";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to process PDF: " + e.getMessage();
        }
    }

    public List<String> getUploadedFileNamesByCategory(String email, String category) {
        User user = userRepository.findByEmail(email).orElseThrow();
        String normalizedCategory = normalizeCategory(category);
        if (normalizedCategory == null) {
            return List.of();
        }

        return chunkRepository.findDistinctFileNamesByUserIdAndCategory(
                user.getId(), normalizedCategory);
    }

    public List<String> getUploadedFileNames(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return chunkRepository.findByUserId(user.getId())
                .stream()
                .map(DocumentChunk::getFileName)
                .distinct()
                .toList();
    }

    public String normalizeCategory(String category) {
        if (category == null) {
            return null;
        }
        return switch (category.toUpperCase().trim()) {
            case "PSYCHOLOGY", "HUMAN PSYCHOLOGY" -> "PSYCHOLOGY";
            case "FINANCE" -> "FINANCE";
            case "SPIRITUALITY" -> "SPIRITUALITY";
            default -> null;
        };
    }

    private String sectionLabel(String category) {
        return switch (category) {
            case "PSYCHOLOGY" -> "Human Psychology";
            case "FINANCE" -> "Finance";
            case "SPIRITUALITY" -> "Spirituality";
            default -> category;
        };
    }

    private String cleanFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        return fileName.replace("\\", "/")
                .substring(fileName.replace("\\", "/").lastIndexOf("/") + 1)
                .trim();
    }

    private List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String chunk = text.substring(start, end).trim();
            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }
            start += chunkSize - overlap;
        }
        return chunks;
    }
}
