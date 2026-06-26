package com.bhumi.ragbot_ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    // Store embedding as comma-separated TEXT; no pgvector needed.
    @Column(columnDefinition = "TEXT")
    private String embedding;

    private String fileName;

    // "PSYCHOLOGY" | "FINANCE" | "SPIRITUALITY"
    @Column(length = 32)
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public DocumentChunk() {}

    public Long getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getEmbedding() { return embedding; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
