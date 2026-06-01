package com.neuralforge.service;

import com.neuralforge.ai.IntentToFeatureEngine;
import com.neuralforge.model.AIFeatureRequest;
import com.neuralforge.model.User;
import com.neuralforge.repository.AIFeatureRequestRepository;
import com.neuralforge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIFeatureService {

    private final IntentToFeatureEngine intentEngine;
    private final AIFeatureRequestRepository featureRequestRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public AIFeatureRequest createFeatureRequest(String userId, String intent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAiCredits() <= 0) {
            throw new RuntimeException("Insufficient AI credits");
        }

        AIFeatureRequest request = AIFeatureRequest.builder()
                .user(user)
                .intent(intent)
                .status(AIFeatureRequest.Status.PENDING)
                .build();

        AIFeatureRequest saved = featureRequestRepository.save(request);
        processFeatureAsync(saved.getId(), intent, userId);
        return saved;
    }

    @Async
    @Transactional
    public void processFeatureAsync(String requestId, String intent, String userId) {
        AIFeatureRequest request = featureRequestRepository.findById(requestId).orElseThrow();
        request.setStatus(AIFeatureRequest.Status.PROCESSING);
        featureRequestRepository.save(request);

        // Notify frontend via WebSocket
        messagingTemplate.convertAndSendToUser(userId, "/queue/ai-progress",
                new ProgressUpdate(requestId, "PROCESSING", "AI is generating your feature..."));

        try {
            IntentToFeatureEngine.GeneratedFeature feature = intentEngine.generateFromIntent(intent);

            request.setStatus(AIFeatureRequest.Status.COMPLETED);
            request.setGeneratedCode(feature.generatedCode());
            request.setGeneratedFiles(feature.generatedFiles());
            request.setTokensUsed(feature.tokensUsed());
            request.setProcessingTimeMs(feature.processingTimeMs());
            featureRequestRepository.save(request);

            // Deduct AI credits
            User user = userRepository.findById(userId).orElseThrow();
            user.setAiCredits(user.getAiCredits() - 10);
            user.setTotalFeaturesGenerated(user.getTotalFeaturesGenerated() + 1);
            userRepository.save(user);

            messagingTemplate.convertAndSendToUser(userId, "/queue/ai-progress",
                    new ProgressUpdate(requestId, "COMPLETED", "Feature generated successfully!"));

        } catch (Exception e) {
            log.error("Feature generation failed: {}", e.getMessage());
            request.setStatus(AIFeatureRequest.Status.FAILED);
            request.setErrorMessage(e.getMessage());
            featureRequestRepository.save(request);

            messagingTemplate.convertAndSendToUser(userId, "/queue/ai-progress",
                    new ProgressUpdate(requestId, "FAILED", "Generation failed: " + e.getMessage()));
        }
    }

    public Page<AIFeatureRequest> getUserFeatures(String userId, Pageable pageable) {
        return featureRequestRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public record ProgressUpdate(String requestId, String status, String message) {}
}
