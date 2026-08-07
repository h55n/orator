package com.nexoraa.interviewgen.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reference")
public class ReferenceController {

    private static final List<String> JOB_ROLES = List.of(
        "Java Developer","React Developer","Full Stack Developer",
        "Frontend Developer","Backend Developer","Data Analyst",
        "Data Engineer","DevOps Engineer","Cloud Engineer",
        "Android Developer","iOS Developer","QA Engineer",
        "Machine Learning Engineer","Business Analyst","System Architect"
    );

    private static final List<String> TECHNOLOGIES = List.of(
        "Java","Spring Boot","React.js","Angular","Vue.js","Node.js",
        "Python","Django","FastAPI","MySQL","PostgreSQL","MongoDB",
        "Redis","Kafka","Docker","Kubernetes","AWS","Azure","GCP",
        "Git","REST APIs","GraphQL","Microservices","TypeScript","C++","Go"
    );

    @GetMapping("/job-roles")
    public ResponseEntity<Map<String, List<String>>> jobRoles() {
        return ResponseEntity.ok(Map.of("jobRoles", JOB_ROLES));
    }

    @GetMapping("/technologies")
    public ResponseEntity<Map<String, List<String>>> technologies() {
        return ResponseEntity.ok(Map.of("technologies", TECHNOLOGIES));
    }
}
