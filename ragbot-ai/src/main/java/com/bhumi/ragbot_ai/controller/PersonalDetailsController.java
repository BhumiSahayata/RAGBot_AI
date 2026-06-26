package com.bhumi.ragbot_ai.controller;

import com.bhumi.ragbot_ai.dto.PersonalDetailsDto;
import com.bhumi.ragbot_ai.service.PersonalDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class PersonalDetailsController {

    private final PersonalDetailsService personalDetailsService;

    public PersonalDetailsController(PersonalDetailsService personalDetailsService) {
        this.personalDetailsService = personalDetailsService;
    }

    @GetMapping
    public ResponseEntity<PersonalDetailsDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(
                personalDetailsService.getDetails(authentication.getName())
        );
    }

    @PostMapping
    public ResponseEntity<?> saveProfile(
            @RequestBody PersonalDetailsDto dto,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    personalDetailsService.saveDetails(authentication.getName(), dto)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
