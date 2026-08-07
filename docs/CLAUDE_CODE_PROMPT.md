# Claude Code — Continuation Prompt
# InterviewGen Full Stack Project
# Paste this into Claude Code to complete remaining wiring and testing.

---

## Project Context

You are working on **InterviewGen** — a full-stack interview question generator.

**Stack:** Java 17 + Spring Boot 3.2 (backend) | React 18 + Vite (frontend) | MySQL 8

**Directory structure:**
```
interview-gen-project/
├── backend/          ← Spring Boot Maven project
│   └── src/main/java/com/nexoraa/interviewgen/
│       ├── controller/   AuthController, QuestionController, FavoriteController, ReferenceController
│       ├── service/      AuthService, QuestionService, FavoriteService, AiClient, PromptBuilder
│       ├── entity/       User, GenerationSession, Question, UserFavorite
│       ├── repository/   (all Spring Data JPA repos)
│       ├── security/     SecurityConfig, JwtUtil, JwtAuthFilter, AppUserDetailsService
│       ├── dto/          Dto.java (nested static classes)
│       └── exception/    AppException, GlobalExceptionHandler
└── frontend/         ← React + Vite project
    └── src/
        ├── pages/    LoginPage, RegisterPage, DashboardPage, GeneratePage,
        │             SessionPage, HistoryPage, FavoritesPage
        ├── components/layout/  AppLayout
        ├── hooks/    useAuth
        ├── services/ api.js
        └── styles/   design-tokens.css, global.css
```

---

## Tasks for Claude Code to Complete

### 1. Verify & Fix Imports
- Ensure all Java files compile with no missing imports.
- `Dto.java` uses nested static classes — verify all controllers/services import `Dto.RegisterRequest` etc. correctly.
- Run: `cd backend && mvn compile` and fix any errors.

### 2. Fix Dtos.java Conflict
- Delete `backend/src/main/java/com/nexoraa/interviewgen/dto/Dtos.java` (duplicate, package-private).
- Only keep `Dto.java` with public static nested classes.

### 3. JWT Secret Setup
- In `application.properties`, replace `CHANGE_ME_...` with a real Base64-encoded 256-bit key.
- Generate with: `openssl rand -base64 64 | tr -d '\n'`
- Alternatively generate in Java and print on startup (add to ApplicationRunner bean).

### 4. WebClient Bean Config
- Add `@Bean ObjectMapper objectMapper()` in a `@Configuration` class so AiClient can inject it.
- Or annotate AiClient constructor properly for WebClient builder injection.

### 5. Frontend — .env file
Create `frontend/.env`:
```
VITE_API_URL=http://localhost:8080/api
```

### 6. Frontend — Missing favicon
Create `frontend/public/favicon.svg`:
```xml
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32">
  <text y="26" font-size="28">⬡</text>
</svg>
```

### 7. Run Full Integration Test
```bash
# Terminal 1 — Start MySQL, then:
cd backend && mvn spring-boot:run

# Terminal 2:
cd frontend && npm install && npm run dev
```

Then test:
- Register a new user at http://localhost:3000/register
- Login at http://localhost:3000/login
- Generate questions at http://localhost:3000/generate
- Star questions, check /favorites
- Check /history, click a session

### 8. Fix Any Runtime Errors
Common issues to check:
- CORS: if browser blocks requests, verify `app.cors.allowed-origins` includes `http://localhost:3000`
- JWT decode error: ensure secret is valid Base64 ≥ 256 bits
- AI API 401: verify GROQ/NVIDIA key is set in application.properties
- MySQL connection: ensure DB `interviewgen_db` exists (run docs/schema.sql)

### 9. Add Config Class for WebFlux
```java
// backend/.../config/AppConfig.java
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .findAndRegisterModules();
    }
}
```

### 10. Optional Enhancements (if time permits)
- Add loading skeleton to SessionPage while questions load
- Add "Copy question" button on each question card
- Add export-to-PDF button on SessionPage (use browser print)
- Add question count badge on the sidebar nav items
- Add dark mode toggle (CSS variable swap)

---

## API Endpoints Reference

| Method | Endpoint                        | Auth | Description                    |
|--------|---------------------------------|------|--------------------------------|
| POST   | /api/auth/register              | No   | Register new user              |
| POST   | /api/auth/login                 | No   | Login, returns JWT             |
| GET    | /api/auth/me                    | Yes  | Current user profile           |
| POST   | /api/questions/generate         | Yes  | Generate questions via AI      |
| GET    | /api/questions/history          | Yes  | Paginated session history      |
| GET    | /api/questions/session/{id}     | Yes  | Session with all questions     |
| DELETE | /api/questions/session/{id}     | Yes  | Delete a session               |
| GET    | /api/questions/search           | Yes  | Search/filter questions        |
| POST   | /api/favorites/{questionId}     | Yes  | Add to favorites               |
| DELETE | /api/favorites/{questionId}     | Yes  | Remove from favorites          |
| GET    | /api/favorites                  | Yes  | Get all favorites (paginated)  |
| GET    | /api/reference/job-roles        | Yes  | List of job roles              |
| GET    | /api/reference/technologies     | Yes  | List of technologies           |

---

## AI Provider Notes

### GROQ (recommended — completely free)
- Sign up at https://console.groq.com
- Create API key → set in application.properties as `ai.groq.api-key`
- Best model: `llama3-70b-8192` (fast, high quality, free)
- Rate limits: 14,400 req/day on free tier

### NVIDIA NIM (free credits)
- Sign up at https://build.nvidia.com
- Free $25 credits on registration
- Best model: `meta/llama-3.1-70b-instruct`
- Switch by setting `ai.provider=nvidia` in application.properties

---

## Git Setup Commands

```bash
cd interview-gen-project
git init
git add .
git commit -m "feat: initial InterviewGen full-stack implementation

- Spring Boot 3.2 backend with JWT auth, GROQ/NVIDIA AI integration
- React 18 + Vite frontend with Ample Market Minimal design system  
- MySQL schema with users, sessions, questions, favorites
- AI prompt engineering for consistent, high-quality question generation
- Categories: Technical, Coding, SQL, HR, Aptitude
- Difficulty levels: Easy, Medium, Hard
- Experience levels: Fresher, Intermediate, Experienced"

git remote add origin https://github.com/YOUR_USERNAME/interviewgen.git
git push -u origin main
```
