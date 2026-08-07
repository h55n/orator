# InterviewGen — AI-Powered Interview Question Generator

> Submitted for: Nexoraa Technosolve IT Services Pvt. Ltd. — Java Full Stack Developer Assessment

---

## 📌 Project Overview

**InterviewGen** is a full-stack web application that generates targeted, categorized interview questions using free AI APIs (GROQ / NVIDIA NIM). Users register, configure their interview profile, and receive structured questions organized by category and difficulty — all for free.

---

## 🏗️ Technology Stack

| Layer       | Technology                                      |
|-------------|-------------------------------------------------|
| Backend     | Java 17, Spring Boot 3.2, Spring Security       |
| Auth        | JWT (jjwt 0.12.5), BCrypt                      |
| AI Provider | GROQ API (free) or NVIDIA NIM (free credits)   |
| Frontend    | React 18, Vite, React Router v6                |
| Database    | MySQL 8                                         |
| HTTP Client | Spring WebFlux (WebClient) for AI API calls    |
| Version Control | Git + GitHub                               |

---

## ✨ Features

- ✅ User Registration & Login (JWT-secured)
- ✅ Select Job Role, Technology/Skill, and Experience Level
- ✅ AI-generated interview questions (GROQ Llama3 / NVIDIA Llama)
- ✅ Question Categories: Technical, Coding, SQL, HR, Aptitude
- ✅ Difficulty Levels: Easy, Medium, Hard
- ✅ Save Favourite Questions
- ✅ View & Delete Past Sessions (History)
- ✅ Search & Filter Questions
- ✅ Responsive UI (Ample Market Minimal design system)
- ✅ 100% Free — no paid APIs required

---

## 🚀 Setup Instructions

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+
- Maven 3.8+
- GROQ API key (free at https://console.groq.com)

---

### 1. Database Setup

```sql
-- Run docs/schema.sql in MySQL Workbench or CLI:
mysql -u root -p < docs/schema.sql
```

---

### 2. Backend Setup

```bash
cd backend

# Edit application.properties:
# - Set spring.datasource.password=YOUR_MYSQL_PASSWORD
# - Set ai.groq.api-key=YOUR_GROQ_API_KEY
# - Set jwt.secret=<base64 encoded 256-bit key>
#   Generate: openssl rand -base64 64 | tr -d '\n'

mvn spring-boot:run
# Backend runs at http://localhost:8080
```

---

### 3. Frontend Setup

```bash
cd frontend
npm install
npm run dev
# Frontend runs at http://localhost:3000
```

Open http://localhost:3000 and register a new account.

---

## 🔑 Getting Free API Keys

### GROQ (Recommended — Fastest)
1. Go to https://console.groq.com
2. Sign up (free, no credit card)
3. Create API key
4. Set in `application.properties`:
   ```
   ai.provider=groq
   ai.groq.api-key=gsk_xxxxx
   ```
5. Model: `llama3-70b-8192` — 14,400 free requests/day

### NVIDIA NIM (Alternative)
1. Go to https://build.nvidia.com
2. Sign up → get $25 free credits
3. Create API key
4. Set in `application.properties`:
   ```
   ai.provider=nvidia
   ai.nvidia.api-key=nvapi_xxxxx
   ```
5. Model: `meta/llama-3.1-70b-instruct`

---

## 🗄️ Database Schema

```
users
  id, name, email, password, is_active, created_at, updated_at

generation_sessions
  id, user_id (FK), job_role, technology, experience_level,
  categories, question_count, created_at

questions
  id, session_id (FK), question (TEXT), hint (TEXT),
  category, difficulty, job_role, technology, experience_level, created_at

user_favorites
  id, user_id (FK), question_id (FK), created_at
```

---

## 📡 API Endpoints

| Method | Endpoint                        | Auth | Description                  |
|--------|---------------------------------|------|------------------------------|
| POST   | `/api/auth/register`            | ❌   | Register                     |
| POST   | `/api/auth/login`               | ❌   | Login → JWT                  |
| GET    | `/api/auth/me`                  | ✅   | Current user                 |
| POST   | `/api/questions/generate`       | ✅   | AI question generation       |
| GET    | `/api/questions/history`        | ✅   | Paginated session history    |
| GET    | `/api/questions/session/{id}`   | ✅   | Session + questions          |
| DELETE | `/api/questions/session/{id}`   | ✅   | Delete session               |
| GET    | `/api/questions/search`         | ✅   | Search/filter questions      |
| POST   | `/api/favorites/{questionId}`   | ✅   | Add to favorites             |
| DELETE | `/api/favorites/{questionId}`   | ✅   | Remove from favorites        |
| GET    | `/api/favorites`                | ✅   | Get paginated favorites      |
| GET    | `/api/reference/job-roles`      | ✅   | Job roles list               |
| GET    | `/api/reference/technologies`   | ✅   | Technologies list            |

---

## 🤖 AI Prompt Engineering

The `PromptBuilder` service constructs structured prompts that produce consistent output across any LLM:

- **System role priming** — Model is positioned as a "senior technical interviewer"
- **Explicit JSON schema** — Forces structured output with exact field names
- **Per-category distribution rules** — Technical, Coding, SQL, HR, Aptitude guidelines
- **Experience calibration** — Fresher / Intermediate / Experienced guidance blocks
- **Difficulty anchoring** — Easy (recall), Medium (application), Hard (design/optimisation)
- **Anti-hallucination guardrails** — No filler, no duplicates, real-world relevance

---

## 📁 Project Structure

```
interview-gen-project/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/nexoraa/interviewgen/
│       │   ├── InterviewGenApplication.java
│       │   ├── config/         AppConfig.java
│       │   ├── controller/     Auth, Question, Favorite, Reference
│       │   ├── dto/            Dto.java (nested static DTOs)
│       │   ├── entity/         User, GenerationSession, Question, UserFavorite
│       │   ├── exception/      AppException, GlobalExceptionHandler
│       │   ├── repository/     4 Spring Data JPA repos
│       │   ├── security/       SecurityConfig, JwtUtil, JwtAuthFilter
│       │   └── service/        Auth, Question, Favorite, AiClient, PromptBuilder
│       └── resources/
│           └── application.properties
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── App.jsx
│       ├── main.jsx
│       ├── components/layout/  AppLayout
│       ├── hooks/              useAuth
│       ├── pages/              Login, Register, Dashboard, Generate, Session, History, Favorites
│       ├── services/           api.js
│       └── styles/             design-tokens.css, global.css
└── docs/
    ├── schema.sql
    ├── CLAUDE_CODE_PROMPT.md
    └── README.md  ← this file
```

---

## 🎨 Design System

Frontend uses the **Ample Market Minimal** design system:
- Primary: `#111111` (ink black)
- Neutral: `#F6F5F3` (warm off-white background)
- Surface: `#FFFFFF` (cards)
- Card accent: `#FFEF99` (warm yellow highlight)
- Typography: Inter (Google Fonts) — negative letter-spacing editorial feel
- Shape: 8–16px radii, flat hierarchy, minimal shadows

---

## 👨‍💻 Author

Submitted by: [YOUR NAME]  
GitHub: https://github.com/YOUR_USERNAME/interviewgen  
Email: your@email.com
