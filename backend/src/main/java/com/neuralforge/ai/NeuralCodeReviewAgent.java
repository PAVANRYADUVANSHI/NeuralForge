package com.neuralforge.ai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NeuralCodeReviewAgent {

    private final OpenAiChatModel chatModel;

    private static final String ARCHITECT_PROMPT = """
        You are Alex — a Senior Software Architect with 15 years of Java experience.
        Review the code from an architectural perspective: design patterns, SOLID principles,
        scalability, and maintainability. Be direct and critical. Format as JSON with key "architect".
        """;

    private static final String SECURITY_PROMPT = """
        You are Sam — a Security Engineer specializing in Java application security.
        Review the code for OWASP Top 10 vulnerabilities, injection attacks, authentication flaws,
        and data exposure risks. Be thorough. Format as JSON with key "security".
        """;

    private static final String PERFORMANCE_PROMPT = """
        You are Jordan — a Performance Engineer specializing in Java optimization.
        Review the code for N+1 queries, memory leaks, inefficient algorithms, missing indexes,
        and concurrency issues. Format as JSON with key "performance".
        """;

    public CodeReviewResult reviewCode(String code, String language) {
        log.info("Starting Neural Code Review with 3 AI agents");

        String architectReview = chatModel.generate(
                SystemMessage.from(ARCHITECT_PROMPT),
                UserMessage.from("Review this code:\n```" + language + "\n" + code + "\n```")
        ).content().text();

        String securityReview = chatModel.generate(
                SystemMessage.from(SECURITY_PROMPT),
                UserMessage.from("Review this code:\n```" + language + "\n" + code + "\n```")
        ).content().text();

        String performanceReview = chatModel.generate(
                SystemMessage.from(PERFORMANCE_PROMPT),
                UserMessage.from("Review this code:\n```" + language + "\n" + code + "\n```")
        ).content().text();

        return new CodeReviewResult(architectReview, securityReview, performanceReview,
                calculateOverallScore(architectReview, securityReview, performanceReview));
    }

    private int calculateOverallScore(String... reviews) {
        long issueCount = 0;
        for (String review : reviews) {
            issueCount += review.toLowerCase().chars()
                    .filter(c -> review.toLowerCase().indexOf("issue") == c).count();
        }
        return Math.max(0, 100 - (int)(issueCount * 5));
    }

    public record CodeReviewResult(
            String architectFeedback,
            String securityFeedback,
            String performanceFeedback,
            int overallScore
    ) {}
}
