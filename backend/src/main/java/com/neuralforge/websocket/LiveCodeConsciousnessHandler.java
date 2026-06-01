package com.neuralforge.websocket;

import com.neuralforge.ai.IntentToFeatureEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LiveCodeConsciousnessHandler {

    private final IntentToFeatureEngine intentEngine;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/code-change")
    @SendToUser("/queue/ai-suggestions")
    public AISuggestion onCodeChange(@Payload CodeChangeEvent event) {
        log.info("Live code change received from user: {}", event.userId());

        // AI analyzes the code change and suggests next steps
        String suggestion = intentEngine.generateFromIntent(
                "Given this code change: " + event.codeSnippet() +
                " — what should the developer implement next? Give a 1-sentence suggestion."
        ).generatedCode();

        AISuggestion aiSuggestion = new AISuggestion(
                event.userId(),
                suggestion,
                "NEXT_STEP",
                System.currentTimeMillis()
        );

        // Broadcast to team channel
        messagingTemplate.convertAndSend("/topic/team/" + event.teamId(), aiSuggestion);

        return aiSuggestion;
    }

    @MessageMapping("/request-help")
    @SendToUser("/queue/ai-help")
    public AISuggestion onHelpRequest(@Payload HelpRequest request) {
        String help = intentEngine.generateFromIntent(request.question()).generatedCode();
        return new AISuggestion(request.userId(), help, "HELP_RESPONSE", System.currentTimeMillis());
    }

    public record CodeChangeEvent(String userId, String teamId, String codeSnippet, String fileName) {}
    public record HelpRequest(String userId, String question) {}
    public record AISuggestion(String userId, String suggestion, String type, long timestamp) {}
}
