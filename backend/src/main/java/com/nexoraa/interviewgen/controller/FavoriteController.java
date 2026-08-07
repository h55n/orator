package com.nexoraa.interviewgen.controller;

import com.nexoraa.interviewgen.dto.Dto.QuestionDto;
import com.nexoraa.interviewgen.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{questionId}")
    public ResponseEntity<Void> add(@PathVariable Long questionId,
                                    @AuthenticationPrincipal UserDetails user) {
        favoriteService.addFavorite(questionId, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> remove(@PathVariable Long questionId,
                                       @AuthenticationPrincipal UserDetails user) {
        favoriteService.removeFavorite(questionId, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<QuestionDto>> list(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(favoriteService.getFavorites(user.getUsername(), page, size));
    }
}
