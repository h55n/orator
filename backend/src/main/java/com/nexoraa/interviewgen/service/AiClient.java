package com.nexoraa.interviewgen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

/**
 * AiClient — Wraps both GROQ and NVIDIA NIM APIs.
 * Both use the OpenAI-compatible /v1/chat/completions endpoint,
 * so the same code works for either provider.
 *
 * GROQ:  https://api.groq.com/openai/v1
 * NVIDIA: https://integrate.api.nvidia.com/v1
 */
@Slf4j
@Service
public class AiClient {

    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final String model;
    private final int    maxTokens;
    private final double temperature;

    public AiClient(
            @Value("${ai.provider}")          String provider,
            @Value("${ai.groq.api-key:}")     String groqKey,
            @Value("${ai.groq.base-url:}")    String groqUrl,
            @Value("${ai.groq.model:}")       String groqModel,
            @Value("${ai.nvidia.api-key:}")   String nvidiaKey,
            @Value("${ai.nvidia.base-url:}")  String nvidiaUrl,
            @Value("${ai.nvidia.model:}")     String nvidiaModel,
            @Value("${ai.groq.max-tokens:4096}")   int groqMaxTokens,
            @Value("${ai.nvidia.max-tokens:4096}")  int nvidiaMaxTokens,
            @Value("${ai.groq.temperature:0.7}")    double groqTemp,
            @Value("${ai.nvidia.temperature:0.7}")  double nvidiaTemp,
            ObjectMapper mapper) {

        this.mapper = mapper;

        boolean useNvidia = "nvidia".equalsIgnoreCase(provider);
        String  apiKey    = useNvidia ? nvidiaKey    : groqKey;
        String  baseUrl   = useNvidia ? nvidiaUrl    : groqUrl;
        this.model        = useNvidia ? nvidiaModel  : groqModel;
        this.maxTokens    = useNvidia ? nvidiaMaxTokens : groqMaxTokens;
        this.temperature  = useNvidia ? nvidiaTemp   : groqTemp;

        log.info("AI provider: {} | model: {} | base-url: {}", provider, this.model, baseUrl);

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Calls the chat completions endpoint and returns the raw content string.
     */
    public String chat(String systemPrompt, String userPrompt) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("temperature", temperature);
        body.put("max_tokens", maxTokens);
        body.put("messages", List.of(
            Map.of("role", "system", "content", systemPrompt),
            Map.of("role", "user",   "content", userPrompt)
        ));

        try {
            String responseJson = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(55))
                    .block();

            JsonNode root    = mapper.readTree(responseJson);
            String   content = root.path("choices").get(0)
                                   .path("message").path("content").asText();
            log.debug("AI raw content length: {}", content.length());
            return content;

        } catch (Exception e) {
            log.error("AI API call failed: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Parses the JSON response and returns a list of question maps.
     * Handles markdown fences the model may accidentally add.
     */
    public List<Map<String, String>> parseQuestions(String rawContent) {
        try {
            String cleaned = extractJson(rawContent);

            JsonNode root      = mapper.readTree(cleaned);
            JsonNode questions  = root.path("questions");

            List<Map<String, String>> result = new ArrayList<>();
            for (JsonNode q : questions) {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("question",   q.path("question").asText(""));
                map.put("hint",       q.path("hint").asText(""));
                map.put("category",   validateCategory(q.path("category").asText("Technical")));
                map.put("difficulty", validateDifficulty(q.path("difficulty").asText("Medium")));
                if (!map.get("question").isBlank()) result.add(map);
            }
            log.info("Parsed {} questions from AI response", result.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", e.getMessage());
            log.debug("Raw content: {}", rawContent);
            throw new RuntimeException("Failed to parse AI response. Please try again.", e);
        }
    }

    private String validateCategory(String cat) {
        return switch (cat.trim()) {
            case "Technical", "Coding", "SQL", "HR", "Aptitude" -> cat.trim();
            default -> "Technical";
        };
    }

    private String validateDifficulty(String diff) {
        return switch (diff.trim()) {
            case "Easy", "Medium", "Hard" -> diff.trim();
            default -> "Medium";
        };
    }

    public com.nexoraa.interviewgen.dto.Dto.AssessmentResponse parseAssessment(String rawContent) {
        try {
            String cleaned = extractJson(rawContent);
            return mapper.readValue(cleaned, com.nexoraa.interviewgen.dto.Dto.AssessmentResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse AI Assessment response: {}", e.getMessage());
            log.debug("Raw content: {}", rawContent);
            throw new RuntimeException("Failed to parse AI assessment. Please try again.", e);
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start != -1 && end != -1 && end >= start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
