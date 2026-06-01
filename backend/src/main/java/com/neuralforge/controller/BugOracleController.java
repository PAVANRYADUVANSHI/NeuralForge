package com.neuralforge.controller;

import com.neuralforge.ai.NeuralCodeReviewAgent;
import com.neuralforge.model.BugPrediction;
import com.neuralforge.service.BugPredictionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oracle")
@RequiredArgsConstructor
public class BugOracleController {

    private final BugPredictionService bugPredictionService;
    private final NeuralCodeReviewAgent codeReviewAgent;

    @PostMapping("/predict-bugs")
    public ResponseEntity<BugPrediction> predictBugs(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BugAnalysisRequest request) {
        return ResponseEntity.ok(
                bugPredictionService.analyzeFile(
                        userDetails.getUsername(),
                        request.filePath(),
                        request.fileContent()
                )
        );
    }

    @PostMapping("/code-review")
    public ResponseEntity<NeuralCodeReviewAgent.CodeReviewResult> reviewCode(
            @Valid @RequestBody CodeReviewRequest request) {
        return ResponseEntity.ok(
                codeReviewAgent.reviewCode(request.code(), request.language())
        );
    }

    public record BugAnalysisRequest(
            @NotBlank String filePath,
            @NotBlank String fileContent
    ) {}

    public record CodeReviewRequest(
            @NotBlank String code,
            @NotBlank String language
    ) {}
}
