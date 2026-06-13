package com.bhumi.ragbot_ai.repository;

import com.bhumi.ragbot_ai.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByUserId(Long userId);
}