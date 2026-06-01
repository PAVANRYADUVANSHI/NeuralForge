package com.neuralforge.ai;

import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemporalCodeMemory {

    private final OpenAiEmbeddingModel embeddingModel;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String MEMORY_PREFIX = "neuralforge:memory:";

    public void storeDecision(String decisionId, String context, String decision) {
        log.info("Storing architectural decision in temporal memory: {}", decisionId);

        Embedding embedding = embeddingModel.embed(context + " " + decision).content();

        MemoryEntry entry = new MemoryEntry(decisionId, context, decision, embedding.vectorAsList());
        redisTemplate.opsForValue().set(
                MEMORY_PREFIX + decisionId,
                entry,
                30,
                TimeUnit.DAYS
        );

        log.info("Decision stored with {} dimensional vector", embedding.dimension());
    }

    public List<String> recallSimilarDecisions(String query) {
        log.info("Recalling similar decisions for: {}", query);
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        // In production: query Pinecone vector DB for similarity search
        // Here we return cached recent decisions from Redis
        return redisTemplate.keys(MEMORY_PREFIX + "*")
                .stream()
                .limit(5)
                .map(key -> {
                    Object val = redisTemplate.opsForValue().get(key);
                    return val != null ? val.toString() : "";
                })
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public record MemoryEntry(
            String id,
            String context,
            String decision,
            List<Float> vector
    ) {}
}
