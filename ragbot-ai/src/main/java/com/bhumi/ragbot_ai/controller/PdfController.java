package com.bhumi.ragbot_ai.controller;

import com.bhumi.ragbot_ai.service.PdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            Authentication authentication) {

        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No file received. Please select a PDF to upload.");
            }

            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            boolean hasPdfName = fileName != null && fileName.toLowerCase().endsWith(".pdf");
            boolean hasPdfContentType = "application/pdf".equals(contentType);
            if (!hasPdfName && !hasPdfContentType) {
                return ResponseEntity.badRequest()
                        .body("Only PDF files are allowed.");
            }

            if (file.getSize() > PdfService.MAX_FILE_SIZE_BYTES) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("File too large. Maximum allowed size is 10 MB.");
            }

            if (category == null || category.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Please select a section (Psychology, Finance, or Spirituality) before uploading.");
            }

            String result = pdfService.processPdf(file, authentication.getName(), category);

            // Return 400 for validation failures (limit exceeded, etc.)
            if (result.startsWith("Failed") || result.startsWith("Invalid")
                    || result.startsWith("Section limit") || result.startsWith("File too large")
                    || result.startsWith("This PDF") || result.startsWith("Only PDF")) {
                return ResponseEntity.badRequest().body(result);
            }

            return ResponseEntity.ok(result);

        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("File too large. Maximum allowed size is 10 MB.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/files")
    public ResponseEntity<Map<String, List<String>>> getUploadedFiles(
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Map<String, List<String>> result = Map.of(
                    "PSYCHOLOGY",   pdfService.getUploadedFileNamesByCategory(email, "PSYCHOLOGY"),
                    "FINANCE",      pdfService.getUploadedFileNamesByCategory(email, "FINANCE"),
                    "SPIRITUALITY", pdfService.getUploadedFileNamesByCategory(email, "SPIRITUALITY")
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
    }
}
