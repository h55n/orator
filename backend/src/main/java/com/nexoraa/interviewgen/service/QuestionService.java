package com.nexoraa.interviewgen.service;

import com.nexoraa.interviewgen.dto.Dto.*;
import com.nexoraa.interviewgen.entity.*;
import com.nexoraa.interviewgen.exception.AppException;
import com.nexoraa.interviewgen.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final GenerationSessionRepository sessionRepo;
    private final QuestionRepository          questionRepo;
    private final UserFavoriteRepository      favoriteRepo;
    private final UserRepository              userRepo;
    private final AiClient                    aiClient;
    private final PromptBuilder               promptBuilder;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    // ── Generate ──────────────────────────────────────────────

    @Transactional
    public GenerateResponse generate(GenerateRequest req, String email) {
        User user = getUser(email);

        log.info("Generating {} questions for user={} role={} tech={} level={}",
                req.getQuestionCount(), email, req.getJobRole(), req.getTechnology(), req.getExperienceLevel());

        // Build prompts
        String systemPrompt = promptBuilder.buildSystemPrompt(req.getExperienceLevel());
        String userPrompt   = promptBuilder.buildUserPrompt(
                req.getJobRole(), req.getTechnology(),
                req.getExperienceLevel(), req.getCategories(),
                req.getQuestionCount());

        // Call AI
        String rawContent = aiClient.chat(systemPrompt, userPrompt);
        List<Map<String, String>> parsed = aiClient.parseQuestions(rawContent);

        if (parsed.isEmpty()) {
            throw new AppException("AI returned no questions. Please try again.", HttpStatus.BAD_GATEWAY);
        }

        // Persist session
        GenerationSession session = GenerationSession.builder()
                .user(user)
                .jobRole(req.getJobRole())
                .technology(req.getTechnology())
                .experienceLevel(req.getExperienceLevel())
                .categories(String.join(",", req.getCategories()))
                .questionCount(parsed.size())
                .build();
        session = sessionRepo.save(session);

        // Persist questions
        List<Question> questions = new ArrayList<>();
        for (Map<String, String> q : parsed) {
            questions.add(Question.builder()
                    .session(session)
                    .question(q.get("question"))
                    .hint(q.get("hint"))
                    .category(q.get("category"))
                    .difficulty(q.get("difficulty"))
                    .jobRole(req.getJobRole())
                    .technology(req.getTechnology())
                    .experienceLevel(req.getExperienceLevel())
                    .build());
        }
        questions = questionRepo.saveAll(questions);

        List<QuestionDto> dtos = questions.stream()
                .map(q -> toQuestionDto(q, false))
                .collect(Collectors.toList());

        return GenerateResponse.builder()
                .sessionId(session.getId())
                .questions(dtos)
                .build();
    }

    // ── History ───────────────────────────────────────────────

    public Page<SessionSummaryDto> getHistory(String email, int page, int size) {
        User user = getUser(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return sessionRepo.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toSummaryDto);
    }

    // ── Session detail ────────────────────────────────────────

    public SessionDto getSession(Long sessionId, String email) {
        User user = getUser(email);
        GenerationSession session = sessionRepo.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new AppException("Session not found", HttpStatus.NOT_FOUND));

        Set<Long> favIds = getFavoriteIds(user.getId(), session);

        List<QuestionDto> questions = questionRepo.findBySessionId(sessionId).stream()
                .map(q -> toQuestionDto(q, favIds.contains(q.getId())))
                .collect(Collectors.toList());

        return SessionDto.builder()
                .id(session.getId())
                .jobRole(session.getJobRole())
                .technology(session.getTechnology())
                .experienceLevel(session.getExperienceLevel())
                .categories(splitCategories(session.getCategories()))
                .questionCount(questions.size())
                .createdAt(session.getCreatedAt())
                .questions(questions)
                .assessmentData(session.getAssessmentData())
                .build();
    }

    // ── Delete session ────────────────────────────────────────

    @Transactional
    public void deleteSession(Long sessionId, String email) {
        User user = getUser(email);
        GenerationSession session = sessionRepo.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new AppException("Session not found", HttpStatus.NOT_FOUND));
        
        favoriteRepo.deleteBySessionId(sessionId);
        sessionRepo.delete(session);
    }

    // ── Assess Session ────────────────────────────────────────

    @Transactional
    public AssessmentResponse assessSession(Long sessionId, AssessmentRequest req, String email) {
        User user = getUser(email);
        GenerationSession session = sessionRepo.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new AppException("Session not found", HttpStatus.NOT_FOUND));

        List<Question> questions = questionRepo.findBySessionId(sessionId);
        
        // Build QA data string for prompt
        StringBuilder qaData = new StringBuilder();
        for (Question q : questions) {
            AnswerData ans = req.getAnswers().get(q.getId());
            if (ans != null && ans.getText() != null && !ans.getText().isBlank()) {
                qaData.append("Q ID: ").append(q.getId()).append("\n")
                      .append("Question: ").append(q.getQuestion()).append("\n")
                      .append("Answer: ").append(ans.getText()).append("\n")
                      .append("Metrics: PasteCount=").append(ans.getPasteCount())
                      .append(", TabSwitches=").append(ans.getTabSwitches()).append("\n\n");
            }
        }

        if (qaData.length() == 0) {
            throw new AppException("No answers provided for assessment", HttpStatus.BAD_REQUEST);
        }

        String systemPrompt = promptBuilder.buildAssessmentSystemPrompt();
        String userPrompt = promptBuilder.buildAssessmentUserPrompt(
            session.getJobRole(), session.getTechnology(), session.getExperienceLevel(), qaData.toString()
        );

        String rawContent = aiClient.chat(systemPrompt, userPrompt);
        AssessmentResponse response = aiClient.parseAssessment(rawContent);

        try {
            session.setAssessmentData(objectMapper.writeValueAsString(response));
            sessionRepo.save(session);
        } catch (Exception e) {
            log.error("Failed to serialize assessment data", e);
        }

        return response;
    }

    // ── Search ────────────────────────────────────────────────

    public Page<QuestionDto> search(String email, String keyword, String category,
                                     String difficulty, String technology, int page, int size) {
        User user = getUser(email);
        Pageable pageable = PageRequest.of(page, size);
        String kw   = (keyword   != null && keyword.isBlank())   ? null : keyword;
        String cat  = (category  != null && category.isBlank())  ? null : category;
        String diff = (difficulty!= null && difficulty.isBlank()) ? null : difficulty;
        String tech = (technology!= null && technology.isBlank()) ? null : technology;

        return questionRepo.searchQuestions(user.getId(), kw, cat, diff, tech, pageable)
                .map(q -> toQuestionDto(q, false));
    }

    // ── Helpers ───────────────────────────────────────────────

    private User getUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    private Set<Long> getFavoriteIds(Long userId, GenerationSession session) {
        List<Question> qs = questionRepo.findBySessionId(session.getId());
        if (qs.isEmpty()) return Set.of();
        return favoriteRepo.findByUserIdWithQuestion(userId, Pageable.unpaged())
                .stream()
                .map(uf -> uf.getQuestion().getId())
                .collect(Collectors.toSet());
    }

    private QuestionDto toQuestionDto(Question q, boolean isFav) {
        return QuestionDto.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .hint(q.getHint())
                .category(q.getCategory())
                .difficulty(q.getDifficulty())
                .technology(q.getTechnology())
                .jobRole(q.getJobRole())
                .experienceLevel(q.getExperienceLevel())
                .isFavorite(isFav)
                .build();
    }

    private SessionSummaryDto toSummaryDto(GenerationSession s) {
        return SessionSummaryDto.builder()
                .id(s.getId())
                .jobRole(s.getJobRole())
                .technology(s.getTechnology())
                .experienceLevel(s.getExperienceLevel())
                .categories(splitCategories(s.getCategories()))
                .questionCount(s.getQuestionCount())
                .createdAt(s.getCreatedAt())
                .assessmentData(s.getAssessmentData())
                .build();
    }

    private List<String> splitCategories(String cats) {
        if (cats == null || cats.isBlank()) return List.of();
        return Arrays.asList(cats.split(","));
    }
}
