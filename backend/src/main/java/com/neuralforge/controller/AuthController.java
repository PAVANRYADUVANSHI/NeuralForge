package com.neuralforge.controller;

import com.neuralforge.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthService.AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req.username(), req.email(), req.password()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthService.AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req.username(), req.password()));
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password
    ) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}
}
