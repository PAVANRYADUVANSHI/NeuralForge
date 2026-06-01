package com.neuralforge.ai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelfHealingRuntime {

    private final OpenAiChatModel chatModel;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String SYSTEM_PROMPT = """
        You are NeuralForge's Self-Healing Runtime AI.
        
        Given a production error log and the relevant source code, you must:
        1. Identify the root cause
        2. Generate a minimal, safe code patch
        3. Explain the fix
        4. Assess risk level (LOW/MEDIUM/HIGH)
        
        Only suggest patches with LOW or MEDIUM risk for auto-deployment.
        HIGH risk patches require human approval.
        
        Respond in JSON format:
        {
          "rootCause": "...",
          "patch": "...",
          "explanation": "...",
          "riskLevel": "LOW|MEDIUM|HIGH",
          "autoDeployable": true|false,
          "testCommands": ["mvn test -Dtest=..."]
        }
        """;

    @KafkaListener(topics = "production-errors", groupId = "self-healing-agent")
    public void onProductionError(ProductionErrorEvent event) {
        log.warn("Self-Healing Runtime activated for error: {}", event.errorType());

        HealingPatch patch = generatePatch(event.errorLog(), event.sourceCode());

        if (patch.autoDeployable()) {
            log.info("Auto-deploying patch for: {}", event.errorType());
            kafkaTemplate.send("deploy-patches", patch);
        } else {
            log.warn("HIGH risk patch — sending for human review: {}", event.errorType());
            kafkaTemplate.send("human-review-queue", patch);
        }
    }

    public HealingPatch generatePatch(String errorLog, String sourceCode) {
        String response = chatModel.generate(
                SystemMessage.from(SYSTEM_PROMPT),
                UserMessage.from("Error Log:\n" + errorLog + "\n\nSource Code:\n" + sourceCode)
        ).content().text();

        return parseHealingPatch(response);
    }

    private HealingPatch parseHealingPatch(String json) {
        boolean autoDeployable = json.contains("\"autoDeployable\": true");
        String riskLevel = json.contains("\"riskLevel\": \"LOW\"") ? "LOW" :
                           json.contains("\"riskLevel\": \"MEDIUM\"") ? "MEDIUM" : "HIGH";
        return new HealingPatch(json, riskLevel, autoDeployable);
    }

    public record ProductionErrorEvent(String errorType, String errorLog, String sourceCode) {}
    public record HealingPatch(String patchDetails, String riskLevel, boolean autoDeployable) {}
}
