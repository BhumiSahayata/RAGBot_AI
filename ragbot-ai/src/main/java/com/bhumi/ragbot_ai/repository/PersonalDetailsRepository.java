package com.bhumi.ragbot_ai.repository;

import com.bhumi.ragbot_ai.entity.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalDetailsRepository
        extends JpaRepository<PersonalDetails, Long> {

    Optional<PersonalDetails> findByUserId(Long userId);
}