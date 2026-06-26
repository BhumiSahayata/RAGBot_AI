package com.bhumi.ragbot_ai.repository;

import com.bhumi.ragbot_ai.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    // Existing: kept for backward compatibility.
    List<DocumentChunk> findByUserId(Long userId);

    // Fetch chunks for a specific user and category.
    List<DocumentChunk> findByUserIdAndCategory(Long userId, String category);

    // Count books (distinct filenames) per user and category.
    @Query("""
            select count(distinct dc.fileName)
            from DocumentChunk dc
            where dc.user.id = :userId and dc.category = :category
            """)
    long countDistinctFilesByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") String category);

    // Check if a specific file already exists for user and category.
    boolean existsByUserIdAndCategoryAndFileName(Long userId, String category, String fileName);

    // Get distinct file names per user and category.
    @Query("""
            select distinct dc.fileName
            from DocumentChunk dc
            where dc.user.id = :userId and dc.category = :category
            order by dc.fileName
            """)
    List<String> findDistinctFileNamesByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") String category);
}
