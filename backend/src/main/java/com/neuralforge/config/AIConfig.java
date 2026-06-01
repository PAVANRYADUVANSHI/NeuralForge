package com.neuralforge.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AIConfig {

    @Value("${neuralforge.ai.openai-api-key}")
    private String openAiApiKey;

    @Value("${neuralforge.ai.model}")
    private String model;

    @Value("${neuralforge.ai.temperature}")
    private Double temperature;

    @Value("${neuralforge.ai.max-tokens}")
    private Integer maxTokens;

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .timeout(Duration.ofSeconds(120))
                .logRequests(false)
                .logResponses(false)
                .build();
    }

    @Bean
    public OpenAiEmbeddingModel openAiEmbeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(openAiApiKey)
                .modelName("text-embedding-3-large")
                .timeout(Duration.ofSeconds(30))
                .build();
    }
}
