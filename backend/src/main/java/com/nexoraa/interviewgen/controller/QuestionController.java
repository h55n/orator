package com.nexoraa.interviewgen.controller;

import com.nexoraa.interviewgen.dto.Dto.*;
import com.nexoraa.interviewgen.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /** Generate questions via AI */
    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generate(
            @Valid @RequestBody GenerateRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.generate(req, user.getUsername()));
    }

    /** Paginated history of sessions */
    @GetMapping("/history")
    public ResponseEntity<Page<SessionSummaryDto>> history(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(questionService.getHistory(user.getUsername(), page, size));
    }

    /** Full session with questions */
    @GetMapping("/session/{id}")
    public ResponseEntity<SessionDto> session(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(questionService.getSession(id, user.getUsername()));
    }

    /** Delete a session */
    @DeleteMapping("/session/{id}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        questionService.deleteSession(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    /** Search across all questions */
    @GetMapping("/search")
    public ResponseEntity<Page<QuestionDto>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String technology,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                questionService.search(user.getUsername(), keyword, category, difficulty, technology, page, size));
    }
}
