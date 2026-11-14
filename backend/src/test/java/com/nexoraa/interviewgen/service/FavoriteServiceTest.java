package com.nexoraa.interviewgen.service;

import com.nexoraa.interviewgen.entity.GenerationSession;
import com.nexoraa.interviewgen.entity.Question;
import com.nexoraa.interviewgen.entity.User;
import com.nexoraa.interviewgen.entity.UserFavorite;
import com.nexoraa.interviewgen.exception.AppException;
import com.nexoraa.interviewgen.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
@Import(FavoriteService.class)
class FavoriteServiceTest {
    @Autowired private FavoriteService service;
    @Autowired private UserRepository users;
    @Autowired private GenerationSessionRepository sessions;
    @Autowired private QuestionRepository questions;
    @Autowired private UserFavoriteRepository favorites;

    private User owner;
    private User other;
    private Question question;

    @BeforeEach void createUsersAndQuestion() {
        owner = users.save(User.builder().name("Owner").email("owner@example.invalid")
                .password("test-only").build());
        other = users.save(User.builder().name("Other").email("other@example.invalid")
                .password("test-only").build());
        GenerationSession session = sessions.save(GenerationSession.builder().user(owner)
                .jobRole("Engineer").technology("Java").experienceLevel("Intermediate")
                .categories("Technical").questionCount(1).build());
        question = questions.save(Question.builder().session(session).question("Owner's question")
                .hint("Owner's hint").category("Technical").difficulty("Easy").build());
    }

    @Test void ownerCanAddReadAndRemoveFavorite() {
        service.addFavorite(question.getId(), owner.getEmail());
        var page = service.getFavorites(owner.getEmail(), 0, 10);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getQuestion()).isEqualTo("Owner's question");
        service.removeFavorite(question.getId(), owner.getEmail());
        assertThat(service.getFavorites(owner.getEmail(), 0, 10)).isEmpty();
    }

    @Test void anotherUserCannotFavoriteQuestionByGuessingItsId() {
        assertThatThrownBy(() -> service.addFavorite(question.getId(), other.getEmail()))
                .isInstanceOfSatisfying(AppException.class, error -> {
                    assertThat(error.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(error.getMessage()).isEqualTo("Question not found");
                });
        assertThat(favorites.count()).isZero();
    }

    @Test void nonexistentQuestionUsesTheSameNotFoundResponse() {
        assertThatThrownBy(() -> service.addFavorite(Long.MAX_VALUE, other.getEmail()))
                .isInstanceOfSatisfying(AppException.class, error -> {
                    assertThat(error.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(error.getMessage()).isEqualTo("Question not found");
                });
        assertThat(favorites.count()).isZero();
    }

    @Test void duplicateOwnedFavoriteRemainsAConflict() {
        service.addFavorite(question.getId(), owner.getEmail());
        assertThatThrownBy(() -> service.addFavorite(question.getId(), owner.getEmail()))
                .isInstanceOfSatisfying(AppException.class,
                        error -> assertThat(error.getStatus()).isEqualTo(HttpStatus.CONFLICT));
        assertThat(favorites.count()).isEqualTo(1);
    }

    @Test void preexistingCrossUserFavoriteIsNotReadable() {
        favorites.saveAndFlush(UserFavorite.builder().user(other).question(question).build());
        var page = service.getFavorites(other.getEmail(), 0, 1);
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
    }

    @Test void preexistingCrossUserFavoriteCannotRevealItselfAsDuplicate() {
        favorites.saveAndFlush(UserFavorite.builder().user(other).question(question).build());
        assertThatThrownBy(() -> service.addFavorite(question.getId(), other.getEmail()))
                .isInstanceOfSatisfying(AppException.class,
                        error -> assertThat(error.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test void ownershipFilteringHappensBeforePaginationAndCounting() {
        favorites.saveAndFlush(UserFavorite.builder().user(other).question(question).build());
        GenerationSession session = sessions.save(GenerationSession.builder().user(other)
                .jobRole("Engineer").technology("Java").experienceLevel("Intermediate")
                .categories("Technical").questionCount(2).build());
        for (int i = 0; i < 2; i++) {
            Question own = questions.save(Question.builder().session(session).question("Other's " + i)
                    .category("Technical").difficulty("Easy").build());
            favorites.saveAndFlush(UserFavorite.builder().user(other).question(own).build());
        }
        var first = favorites.findByUserIdWithQuestion(other.getId(), PageRequest.of(0, 1));
        var second = favorites.findByUserIdWithQuestion(other.getId(), PageRequest.of(1, 1));
        assertThat(first.getTotalElements()).isEqualTo(2);
        assertThat(second.getTotalElements()).isEqualTo(2);
        assertThat(first.getContent()).allSatisfy(fav ->
                assertThat(fav.getQuestion().getSession().getUser().getId()).isEqualTo(other.getId()));
        assertThat(second.getContent()).allSatisfy(fav ->
                assertThat(fav.getQuestion().getSession().getUser().getId()).isEqualTo(other.getId()));
    }
}
