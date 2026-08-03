package com.smartjobportal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.Map;

@Service
public class AiFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(AiFeedbackService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${ai.feedback.bedrock.region:us-east-1}")
    private String region;

    @Value("${ai.feedback.bedrock.model-id:anthropic.claude-3-haiku-20240307-v1:0}")
    private String modelId;

    @Value("${ai.feedback.enabled:false}")
    private boolean enabled;

    /**
     * Calls AWS Bedrock (Claude) to score and give feedback on a mock interview answer.
     * Returns null if AI is disabled or unavailable — caller should fall back to rule-based scoring.
     */
    public AiScore score(String question, String category, String answer, String targetRole) {
        if (!enabled || answer == null || answer.isBlank()) return null;
        try {
            BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            String prompt = buildPrompt(question, category, answer, targetRole);
            String requestBody = MAPPER.writeValueAsString(Map.of(
                    "anthropic_version", "bedrock-2023-05-31",
                    "max_tokens", 512,
                    "messages", new Object[]{
                            Map.of("role", "user", "content", prompt)
                    }
            ));

            InvokeModelResponse response = client.invokeModel(InvokeModelRequest.builder()
                    .modelId(modelId)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(requestBody))
                    .build());

            return parseResponse(response.body().asUtf8String());
        } catch (Exception e) {
            log.warn("AI feedback unavailable, falling back to rule-based scoring: {}", e.getMessage());
            return null;
        }
    }

    private String buildPrompt(String question, String category, String answer, String targetRole) {
        return """
                You are an expert interview coach evaluating a mock interview answer.

                Role being interviewed for: %s
                Question category: %s
                Interview question: %s

                Candidate's answer:
                %s

                Evaluate the answer and respond with ONLY valid JSON in this exact format:
                {
                  "score": <integer 0-100>,
                  "feedback": "<2-4 sentences: what was done well, what to improve, and one specific tip>"
                }

                Scoring guide:
                - 0-30: Very weak — vague, off-topic, or too short
                - 31-60: Adequate — some relevant content but lacks structure or impact
                - 61-80: Good — clear, structured, relevant with some measurable outcome
                - 81-100: Excellent — STAR structure, specific metrics, directly relevant to the role
                """.formatted(targetRole, category, question, answer);
    }

    private AiScore parseResponse(String responseBody) throws Exception {
        JsonNode root = MAPPER.readTree(responseBody);
        String text = root.path("content").get(0).path("text").asText();
        // Extract JSON block from the model's text response
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start == -1 || end == -1) throw new IllegalStateException("No JSON in AI response");
        JsonNode result = MAPPER.readTree(text.substring(start, end + 1));
        int score = Math.min(100, Math.max(0, result.path("score").asInt()));
        String feedback = result.path("feedback").asText("No feedback provided.");
        return new AiScore(score, feedback);
    }

    public record AiScore(int score, String feedback) { }
}
