package com.bhumi.ragbot_ai.service;

import com.bhumi.ragbot_ai.dto.LoginRequest;
import com.bhumi.ragbot_ai.dto.LoginResponse;
import com.bhumi.ragbot_ai.dto.RegisterRequest;
import com.bhumi.ragbot_ai.entity.User;
import com.bhumi.ragbot_ai.repository.UserRepository;
import com.bhumi.ragbot_ai.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        return "User Registered Successfully";
    }

    public LoginResponse loginUser(
            LoginRequest request) {

        Optional<User> userOptional =
                userRepository.findByEmail(
                        request.getEmail());

        if (userOptional.isEmpty()) {

            return new LoginResponse(
                    "INVALID_CREDENTIALS",
                    null,
                    null
            );
        }

        User user = userOptional.get();

        boolean matches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!matches) {

            return new LoginResponse(
                    "INVALID_CREDENTIALS",
                    null,
                    null
            );
        }

        String token =
                jwtService.generateToken(
                        user.getEmail());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getName()
        );
    }
}