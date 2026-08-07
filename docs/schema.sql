-- ============================================================
-- InterviewGen — MySQL Database Schema
-- Run this once to create the database and tables
-- ============================================================

CREATE DATABASE IF NOT EXISTS interviewgen_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE interviewgen_db;

-- Users
CREATE TABLE IF NOT EXISTS users (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100)        NOT NULL,
  email      VARCHAR(255)        NOT NULL UNIQUE,
  password   VARCHAR(255)        NOT NULL,
  is_active  TINYINT(1)          NOT NULL DEFAULT 1,
  created_at DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Generation sessions
CREATE TABLE IF NOT EXISTS generation_sessions (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT       NOT NULL,
  job_role         VARCHAR(100) NOT NULL,
  technology       VARCHAR(100) NOT NULL,
  experience_level VARCHAR(50)  NOT NULL,
  categories       VARCHAR(255) NOT NULL,
  question_count   INT          DEFAULT 0,
  created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_sessions_user (user_id),
  INDEX idx_sessions_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Questions
CREATE TABLE IF NOT EXISTS questions (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id       BIGINT       NOT NULL,
  question         TEXT         NOT NULL,
  hint             TEXT,
  category         VARCHAR(50)  NOT NULL,
  difficulty       VARCHAR(20)  NOT NULL,
  job_role         VARCHAR(100),
  technology       VARCHAR(100),
  experience_level VARCHAR(50),
  created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES generation_sessions(id) ON DELETE CASCADE,
  INDEX idx_questions_session  (session_id),
  INDEX idx_questions_category (category),
  INDEX idx_questions_difficulty(difficulty),
  FULLTEXT INDEX ft_questions_text (question)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Favorites (user ↔ question many-to-many)
CREATE TABLE IF NOT EXISTS user_favorites (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT   NOT NULL,
  question_id BIGINT   NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)     REFERENCES users(id)     ON DELETE CASCADE,
  FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
  UNIQUE KEY uq_user_question (user_id, question_id),
  INDEX idx_favorites_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
