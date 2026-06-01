package com.neuralforge.service;

import com.neuralforge.model.User;
import com.neuralforge.repository.UserRepository;
import com.neuralforge.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) throw new RuntimeException("Username already taken");
        if (userRepository.existsByEmail(email)) throw new RuntimeException("Email already registered");

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        return buildResponse(jwtTokenProvider.generateToken(auth), username, user);
    }

    public AuthResponse login(String usernameOrEmail, String password) {
        // Resolve username from email or username input
        String username = usernameOrEmail;
        if (usernameOrEmail.contains("@")) {
            username = userRepository.findByEmail(usernameOrEmail)
                    .map(User::getUsername)
                    .orElseThrow(() -> new RuntimeException("No account found with this email"));
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        User user = userRepository.findByUsername(username).orElseThrow();
        return buildResponse(jwtTokenProvider.generateToken(auth), username, user);
    }

    private AuthResponse buildResponse(String token, String username, User user) {
        return new AuthResponse(
                token,
                jwtTokenProvider.generateRefreshToken(username),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getAiCredits()
        );
    }

    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String userId,
            String username,
            String email,
            String role,
            int aiCredits
    ) {}
}
