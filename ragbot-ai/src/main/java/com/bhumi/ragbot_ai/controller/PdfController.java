package com.bhumi.ragbot_ai.controller;

import com.bhumi.ragbot_ai.service.PdfService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/upload")
    public String uploadPdf(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        return pdfService.processPdf(
                file,
                authentication.getName()
        );
    }

    @GetMapping("/files")
    public List<String> getUploadedFiles(
            Authentication authentication) {

        return pdfService.getUploadedFileNames(
                authentication.getName());
    }
}