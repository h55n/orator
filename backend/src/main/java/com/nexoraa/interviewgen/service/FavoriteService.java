package com.nexoraa.interviewgen.service;

import com.nexoraa.interviewgen.dto.Dto.*;
import com.nexoraa.interviewgen.entity.*;
import com.nexoraa.interviewgen.exception.AppException;
import com.nexoraa.interviewgen.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserFavoriteRepository favoriteRepo;
    private final QuestionRepository     questionRepo;
    private final UserRepository         userRepo;

    @Transactional
    public void addFavorite(Long questionId, String email) {
        User user = getUser(email);
        if (favoriteRepo.existsByUserIdAndQuestionId(user.getId(), questionId)) {
            throw new AppException("Already in favorites", HttpStatus.CONFLICT);
        }
        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new AppException("Question not found", HttpStatus.NOT_FOUND));
        favoriteRepo.save(UserFavorite.builder().user(user).question(question).build());
    }

    @Transactional
    public void removeFavorite(Long questionId, String email) {
        User user = getUser(email);
        UserFavorite fav = favoriteRepo.findByUserIdAndQuestionId(user.getId(), questionId)
                .orElseThrow(() -> new AppException("Favorite not found", HttpStatus.NOT_FOUND));
        favoriteRepo.delete(fav);
    }

    public Page<QuestionDto> getFavorites(String email, int page, int size) {
        User user = getUser(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return favoriteRepo.findByUserIdWithQuestion(user.getId(), pageable)
                .map(uf -> {
                    Question q = uf.getQuestion();
                    return QuestionDto.builder()
                            .id(q.getId())
                            .question(q.getQuestion())
                            .hint(q.getHint())
                            .category(q.getCategory())
                            .difficulty(q.getDifficulty())
                            .technology(q.getTechnology())
                            .jobRole(q.getJobRole())
                            .experienceLevel(q.getExperienceLevel())
                            .isFavorite(true)
                            .build();
                });
    }

    private User getUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }
}
