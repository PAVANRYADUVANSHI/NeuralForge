package com.neuralforge.ai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictiveBugOracle {

    private final OpenAiChatModel chatModel;

    private static final String SYSTEM_PROMPT = """
        You are NeuralForge's Predictive Bug Oracle — an AI that analyzes Java source code
        and predicts bugs BEFORE they occur at runtime.
        
        Analyze the provided Java code and identify:
        1. NullPointerExceptions (unguarded null dereferences)
        2. ConcurrentModificationExceptions (unsafe collection iteration)
        3. Resource leaks (unclosed streams, connections)
        4. SQL injection vulnerabilities
        5. Race conditions in multi-threaded code
        6. Memory leaks (static references, listener not removed)
        7. Integer overflow/underflow
        8. Incorrect equals/hashCode implementations
        9. Transactional boundary violations
        10. Security vulnerabilities (OWASP Top 10)
        
        For each bug found, respond in this EXACT JSON array format:
        [
          {
            "lineNumber": 42,
            "bugType": "NullPointerException",
            "severity": "CRITICAL",
            "confidence": 94.5,
            "description": "Variable 'user' may be null when calling user.getName()",
            "suggestedFix": "Add null check: if (user != null) { ... }"
          }
        ]
        
        Only respond with the JSON array. No other text.
        """;

    public List<BugPredictionResult> analyzeCode(String filePath, String fileContent) {
        log.info("Running Predictive Bug Oracle on: {}", filePath);

        String prompt = String.format("Analyze this Java file (%s):\n\n```java\n%s\n```", filePath, fileContent);

        String response = chatModel.generate(
                SystemMessage.from(SYSTEM_PROMPT),
                UserMessage.from(prompt)
        ).content().text();

        return parseBugPredictions(response);
    }

    private List<BugPredictionResult> parseBugPredictions(String jsonResponse) {
        List<BugPredictionResult> results = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(
                    "\"lineNumber\":\\s*(\\d+).*?\"bugType\":\\s*\"([^\"]+)\".*?" +
                    "\"severity\":\\s*\"([^\"]+)\".*?\"confidence\":\\s*([\\d.]+).*?" +
                    "\"description\":\\s*\"([^\"]+)\".*?\"suggestedFix\":\\s*\"([^\"]+)\"",
                    Pattern.DOTALL
            );
            Matcher matcher = pattern.matcher(jsonResponse);
            while (matcher.find()) {
                results.add(new BugPredictionResult(
                        Integer.parseInt(matcher.group(1)),
                        matcher.group(2),
                        matcher.group(3),
                        Double.parseDouble(matcher.group(4)),
                        matcher.group(5),
                        matcher.group(6)
                ));
            }
        } catch (Exception e) {
            log.error("Failed to parse bug predictions: {}", e.getMessage());
        }
        return results;
    }

    public record BugPredictionResult(
            int lineNumber,
            String bugType,
            String severity,
            double confidence,
            String description,
            String suggestedFix
    ) {}
}
