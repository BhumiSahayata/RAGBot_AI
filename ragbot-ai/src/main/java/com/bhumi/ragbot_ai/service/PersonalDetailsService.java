package com.bhumi.ragbot_ai.service;

import com.bhumi.ragbot_ai.dto.PersonalDetailsDto;
import com.bhumi.ragbot_ai.entity.PersonalDetails;
import com.bhumi.ragbot_ai.entity.User;
import com.bhumi.ragbot_ai.repository.PersonalDetailsRepository;
import com.bhumi.ragbot_ai.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonalDetailsService {

    private final PersonalDetailsRepository personalDetailsRepository;
    private final UserRepository userRepository;

    public PersonalDetailsService(
            PersonalDetailsRepository personalDetailsRepository,
            UserRepository userRepository) {
        this.personalDetailsRepository = personalDetailsRepository;
        this.userRepository = userRepository;
    }

    public PersonalDetailsDto getDetails(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return personalDetailsRepository.findByUserId(user.getId())
                .map(this::toDto)
                .orElse(new PersonalDetailsDto()); // return empty DTO if not set yet
    }

    public PersonalDetailsDto saveDetails(String email, PersonalDetailsDto dto) {
        User user = userRepository.findByEmail(email).orElseThrow();

        PersonalDetails details = personalDetailsRepository
                .findByUserId(user.getId())
                .orElse(new PersonalDetails());

        details.setUser(user);
        details.setName(clean(dto.getName()));
        details.setAge(validateAge(dto.getAge()));
        details.setOccupation(clean(dto.getOccupation()));
        details.setGoals(clean(dto.getGoals()));
        details.setInterests(clean(dto.getInterests()));
        details.setNotes(clean(dto.getNotes()));

        return toDto(personalDetailsRepository.save(details));
    }

    private PersonalDetailsDto toDto(PersonalDetails pd) {
        PersonalDetailsDto dto = new PersonalDetailsDto();
        dto.setName(pd.getName());
        dto.setAge(pd.getAge());
        dto.setOccupation(pd.getOccupation());
        dto.setGoals(pd.getGoals());
        dto.setInterests(pd.getInterests());
        dto.setNotes(pd.getNotes());
        return dto;
    }

    private Integer validateAge(Integer age) {
        if (age == null) {
            return null;
        }
        if (age < 0 || age > 130) {
            throw new IllegalArgumentException("Age must be between 0 and 130.");
        }
        return age;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
