package com.neuralforge.oracle;

import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RestController
@RequestMapping("/api/memory")
@RequiredArgsConstructor
@Slf4j
public class VectorMemoryOracle {

    private final OpenAiEmbeddingModel embeddingModel;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String VECTOR_PREFIX = "nf:vector:";
    private static final String META_PREFIX = "nf:meta:";

    @PostMapping("/store")
    public Map<String, String> storeMemory(@RequestBody MemoryStoreRequest request) {
        log.info("Storing memory: {}", request.title());

        String content = request.title() + " " + request.content();
        Embedding embedding = embeddingModel.embed(content).content();

        String vectorId = UUID.randomUUID().toString();

        // Store embedding vector
        redisTemplate.opsForValue().set(
                VECTOR_PREFIX + vectorId,
                embedding.vectorAsList(),
                90, TimeUnit.DAYS
        );

        // Store metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("id", vectorId);
        meta.put("title", request.title());
        meta.put("content", request.content());
        meta.put("tags", request.tags());
        meta.put("timestamp", System.currentTimeMillis());
        redisTemplate.opsForHash().putAll(META_PREFIX + vectorId, meta);

        log.info("Stored memory with vectorId: {} ({}D)", vectorId, embedding.dimension());
        return Map.of("vectorId", vectorId, "dimensions", String.valueOf(embedding.dimension()));
    }

    @PostMapping("/recall")
    public List<MemoryRecallResult> recallSimilar(@RequestBody MemoryRecallRequest request) {
        log.info("Recalling memories similar to: {}", request.query());

        Embedding queryEmbedding = embeddingModel.embed(request.query()).content();
        List<Float> queryVector = queryEmbedding.vectorAsList();

        Set<String> keys = redisTemplate.keys(VECTOR_PREFIX + "*");
        if (keys == null || keys.isEmpty()) return Collections.emptyList();

        List<ScoredMemory> scored = new ArrayList<>();
        for (String key : keys) {
            Object stored = redisTemplate.opsForValue().get(key);
            if (stored instanceof List<?> storedVector) {
                double similarity = cosineSimilarity(queryVector, (List<?>) storedVector);
                String vectorId = key.replace(VECTOR_PREFIX, "");
                scored.add(new ScoredMemory(vectorId, similarity));
            }
        }

        return scored.stream()
                .filter(s -> s.score() > 0.75)
                .sorted(Comparator.comparingDouble(ScoredMemory::score).reversed())
                .limit(request.topK())
                .map(s -> {
                    Map<Object, Object> meta = redisTemplate.opsForHash().entries(META_PREFIX + s.vectorId());
                    return new MemoryRecallResult(
                            s.vectorId(),
                            (String) meta.get("title"),
                            (String) meta.get("content"),
                            s.score()
                    );
                })
                .toList();
    }

    private double cosineSimilarity(List<Float> a, List<?> b) {
        if (a.size() != b.size()) return 0.0;
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.size(); i++) {
            double ai = a.get(i);
            double bi = ((Number) b.get(i)).doubleValue();
            dot += ai * bi;
            normA += ai * ai;
            normB += bi * bi;
        }
        return (normA == 0 || normB == 0) ? 0 : dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public record MemoryStoreRequest(String title, String content, String tags) {}
    public record MemoryRecallRequest(String query, int topK) {}
    public record MemoryRecallResult(String id, String title, String content, double similarity) {}
    private record ScoredMemory(String vectorId, double score) {}
}
