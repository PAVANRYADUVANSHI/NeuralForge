package com.neuralforge.agents;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutonomousAgentOrchestrator {

    private final OpenAiChatModel chatModel;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Agent registry — each agent has a specialized role
    private static final Map<String, String> AGENT_PERSONAS = Map.of(
        "ARCHITECT",    "You are an autonomous software architect. Analyze system design and suggest improvements.",
        "OPTIMIZER",    "You are an autonomous performance optimizer. Find bottlenecks and suggest optimizations.",
        "SECURITY",     "You are an autonomous security agent. Continuously scan for vulnerabilities.",
        "DOCUMENTER",   "You are an autonomous documentation agent. Generate and update docs from code changes.",
        "TEST_WRITER",  "You are an autonomous test engineer. Generate comprehensive test cases for new code."
    );

    private final Map<String, AgentState> agentStates = new ConcurrentHashMap<>();

    @KafkaListener(topics = "agent-tasks", groupId = "agent-orchestrator")
    public void onAgentTask(AgentTask task) {
        log.info("Orchestrating agent: {} for task: {}", task.agentType(), task.taskDescription());

        String persona = AGENT_PERSONAS.getOrDefault(task.agentType(), AGENT_PERSONAS.get("ARCHITECT"));

        String result = chatModel.generate(
                SystemMessage.from(persona + "\n\nYou are operating autonomously. Be decisive and thorough."),
                UserMessage.from(task.taskDescription() + "\n\nContext:\n" + task.context())
        ).content().text();

        AgentResult agentResult = new AgentResult(
                task.taskId(),
                task.agentType(),
                result,
                System.currentTimeMillis()
        );

        agentStates.put(task.taskId(), new AgentState(task.taskId(), "COMPLETED", result));
        kafkaTemplate.send("agent-results", agentResult);

        log.info("Agent {} completed task {}", task.agentType(), task.taskId());
    }

    public AgentState getAgentState(String taskId) {
        return agentStates.getOrDefault(taskId, new AgentState(taskId, "NOT_FOUND", null));
    }

    public record AgentTask(String taskId, String agentType, String taskDescription, String context) {}
    public record AgentResult(String taskId, String agentType, String result, long completedAt) {}
    public record AgentState(String taskId, String status, String result) {}
}
