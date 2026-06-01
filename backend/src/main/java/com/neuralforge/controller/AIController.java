package com.neuralforge.controller;

import com.neuralforge.model.AIFeatureRequest;
import com.neuralforge.service.AIFeatureService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIFeatureService featureService;

    @PostMapping("/intent")
    public ResponseEntity<AIFeatureRequest> generateFromIntent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody IntentRequest request) {
        // In production: resolve userId from userDetails
        return ResponseEntity.accepted()
                .body(featureService.createFeatureRequest(userDetails.getUsername(), request.intent()));
    }

    @GetMapping("/features")
    public ResponseEntity<Page<AIFeatureRequest>> getMyFeatures(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(featureService.getUserFeatures(userDetails.getUsername(), pageable));
    }

    public record IntentRequest(@NotBlank String intent) {}
}
