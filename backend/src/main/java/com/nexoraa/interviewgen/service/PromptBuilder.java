package com.nexoraa.interviewgen.service;

import org.springframework.stereotype.Component;
import java.util.List;

/**
 * PromptBuilder — Constructs structured, model-agnostic prompts that produce
 * consistent, high-quality interview questions regardless of the underlying LLM
 * (GROQ Llama3, NVIDIA Mistral/Llama, etc.).
 *
 * Design principles:
 *  1. Role-primed system prompt  — the model "is" a senior technical interviewer
 *  2. Explicit schema in the request — forces JSON-only output with exact keys
 *  3. Per-category difficulty calibration — tells the model exactly what
 *     "Easy / Medium / Hard" means for each category
 *  4. Anti-hallucination guardrails — no filler, no duplicates, real-world focus
 *  5. Chain-of-thought suppressed for speed — output only, no reasoning text
 */
@Component
public class PromptBuilder {

    private static final String SYSTEM_PROMPT = """
You are a senior technical interviewer and curriculum designer with 15+ years of experience \
hiring software engineers at top-tier product companies. \
Your task is to generate precise, relevant, and realistic interview questions.

STRICT RULES:
- Respond ONLY with valid JSON. No markdown, no explanations, no preamble.
- Every question must be unique — no repetition, no paraphrasing of another question.
- Questions must be realistically asked in actual interviews at {EXPERIENCE_LEVEL} level.
- Calibrate difficulty accurately: Easy = concept recall, Medium = application/analysis, \
Hard = design/optimisation/edge-cases.
- Hints must be concise bullet-style key points (not full answers).
- Questions must directly relate to the specified technology and job role.
- Do NOT generate overly generic questions (e.g., "What is OOP?") for Intermediate/Experienced levels.
""";

    private static final String USER_PROMPT_TEMPLATE = """
Generate exactly {COUNT} interview questions for the following profile:

Job Role:        {JOB_ROLE}
Technology:      {TECHNOLOGY}
Experience Level:{EXPERIENCE_LEVEL}
Categories:      {CATEGORIES}

DISTRIBUTION RULES:
- Spread questions across the requested categories as evenly as possible.
- Assign difficulty as: ~30%% Easy, ~45%% Medium, ~25%% Hard.
- For "Coding" questions: include the problem statement clearly; mention expected input/output.
- For "SQL" questions: make them schema-specific to a realistic {TECHNOLOGY} use case.
- For "HR" questions: make them situational and behavioural (STAR format friendly).
- For "Aptitude" questions: logical reasoning relevant to engineering work.
- For "Technical" questions: cover architecture, internals, best practices, system design.

EXPERIENCE CALIBRATION for {EXPERIENCE_LEVEL}:
{EXPERIENCE_GUIDANCE}

OUTPUT FORMAT — return ONLY this JSON structure, no other text:
{
  "questions": [
    {
      "question":  "<full question text>",
      "hint":      "<2-4 bullet key points or approach hints, pipe-separated>",
      "category":  "<Technical|Coding|SQL|HR|Aptitude>",
      "difficulty":"<Easy|Medium|Hard>"
    }
  ]
}
""";

    public String buildSystemPrompt(String experienceLevel) {
        return SYSTEM_PROMPT.replace("{EXPERIENCE_LEVEL}", experienceLevel);
    }

    public String buildUserPrompt(String jobRole, String technology,
                                   String experienceLevel, List<String> categories,
                                   int count) {
        String guidance = experienceGuidance(experienceLevel, technology);
        return USER_PROMPT_TEMPLATE
                .replace("{COUNT}",              String.valueOf(count))
                .replace("{JOB_ROLE}",           jobRole)
                .replace("{TECHNOLOGY}",         technology)
                .replace("{EXPERIENCE_LEVEL}",   experienceLevel)
                .replace("{CATEGORIES}",         String.join(", ", categories))
                .replace("{EXPERIENCE_GUIDANCE}", guidance);
    }

    private String experienceGuidance(String level, String technology) {
        return switch (level) {
            case "Fresher" -> """
                - Focus on fundamental concepts and basic syntax of %s.
                - Questions should test understanding, not deep experience.
                - Coding questions: simple algorithms, basic data structures.
                - Avoid system design or architecture questions.
                - HR questions: academics, learning approach, career goals.
                """.formatted(technology);

            case "Intermediate" -> """
                - Assume 1-3 years of hands-on experience with %s.
                - Questions should test real-world problem-solving ability.
                - Coding questions: medium-difficulty DSA, code optimisation.
                - Technical questions: internals, common pitfalls, debugging.
                - HR questions: project experience, teamwork, conflict resolution.
                """.formatted(technology);

            case "Experienced" -> """
                - Assume 4+ years of experience; possibly leading teams.
                - Questions should challenge design, scalability, and depth.
                - Coding questions: complex algorithms, system-level code.
                - Technical questions: architecture decisions, trade-offs, performance.
                - HR questions: leadership, mentoring, strategic decisions.
                """.formatted(technology);

            default -> "- Match questions to the stated experience level.";
        };
    }
}
