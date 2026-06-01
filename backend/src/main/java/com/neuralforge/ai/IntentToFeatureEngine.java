package com.neuralforge.ai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.data.message.AiMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntentToFeatureEngine {

    private final OpenAiChatModel chatModel;

    private static final String SYSTEM_PROMPT = """
        You are NeuralForge's Intent-to-Feature Engine — the world's most advanced AI code generator.
        
        When given a developer's intent in plain English, you generate a COMPLETE, production-ready Java full-stack feature including:
        1. Spring Boot Controller (REST endpoints)
        2. Service layer with business logic
        3. JPA Entity with proper annotations
        4. Repository interface
        5. DTOs (Request/Response)
        6. React TypeScript component
        7. SQL migration script (Flyway)
        8. Unit test skeleton
        
        Rules:
        - Use Java 21 features (records, sealed classes, pattern matching where appropriate)
        - Follow SOLID principles
        - Include proper error handling
        - Add Swagger/OpenAPI annotations
        - Generate clean, production-ready code
        - Format output as JSON with keys: controller, service, entity, repository, dto, reactComponent, sqlMigration, test
        """;

    public GeneratedFeature generateFromIntent(String intent) {
        log.info("Generating feature from intent: {}", intent);
        long start = System.currentTimeMillis();

        Response<AiMessage> response = chatModel.generate(
                SystemMessage.from(SYSTEM_PROMPT),
                UserMessage.from("Generate a complete Java full-stack feature for: " + intent)
        );

        long elapsed = System.currentTimeMillis() - start;
        String rawCode = response.content().text();

        return GeneratedFeature.builder()
                .intent(intent)
                .generatedCode(rawCode)
                .tokensUsed(response.tokenUsage() != null ? response.tokenUsage().totalTokenCount() : 0)
                .processingTimeMs(elapsed)
                .generatedFiles(extractFileNames(intent))
                .build();
    }

    private List<String> extractFileNames(String intent) {
        String base = intent.replaceAll("[^a-zA-Z0-9 ]", "")
                .trim().split(" ")[0];
        String name = Character.toUpperCase(base.charAt(0)) + base.substring(1).toLowerCase();
        return List.of(
                name + "Controller.java",
                name + "Service.java",
                name + ".java",
                name + "Repository.java",
                name + "Request.java",
                name + "Response.java",
                name + "Component.tsx",
                "V" + System.currentTimeMillis() + "__create_" + name.toLowerCase() + ".sql"
        );
    }

    public record GeneratedFeature(
            String intent,
            String generatedCode,
            Integer tokensUsed,
            Long processingTimeMs,
            List<String> generatedFiles
    ) {
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String intent, generatedCode;
            private Integer tokensUsed;
            private Long processingTimeMs;
            private List<String> generatedFiles;

            public Builder intent(String v) { this.intent = v; return this; }
            public Builder generatedCode(String v) { this.generatedCode = v; return this; }
            public Builder tokensUsed(Integer v) { this.tokensUsed = v; return this; }
            public Builder processingTimeMs(Long v) { this.processingTimeMs = v; return this; }
            public Builder generatedFiles(List<String> v) { this.generatedFiles = v; return this; }
            public GeneratedFeature build() {
                return new GeneratedFeature(intent, generatedCode, tokensUsed, processingTimeMs, generatedFiles);
            }
        }
    }
}
