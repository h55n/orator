package com.nexoraa.interviewgen.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.core.userdetails.User;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {
    private final ApplicationContextRunner context = new ApplicationContextRunner()
            .withUserConfiguration(JwtUtil.class)
            .withPropertyValues("jwt.expiration-ms=60000");

    private static String key(byte value) {
        byte[] bytes = new byte[64];
        java.util.Arrays.fill(bytes, value);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Test void missingKeyFailsAtStartup() {
        context.run(c -> assertThat(c).hasFailed());
    }

    @Test void blankKeyFailsAtStartup() {
        context.withPropertyValues("jwt.secret=").run(c -> assertThat(c).hasFailed());
    }

    @Test void malformedKeyFailsAtStartup() {
        context.withPropertyValues("jwt.secret=not-base64!").run(c -> assertThat(c).hasFailed());
    }

    @Test void shortKeyFailsAtStartup() {
        context.withPropertyValues("jwt.secret=" + Base64.getEncoder().encodeToString(new byte[8]))
                .run(c -> assertThat(c).hasFailed());
    }

    @Test void configuredKeySignsAndRejectsAnotherKey() {
        context.withPropertyValues("jwt.secret=" + key((byte) 7)).run(c -> {
            assertThat(c).hasNotFailed();
            JwtUtil jwt = c.getBean(JwtUtil.class);
            var user = User.withUsername("fixture@example.invalid").password("unused").roles("USER").build();
            String token = jwt.generateToken(user.getUsername());
            assertThat(jwt.isValid(token, user)).isTrue();
            assertThat(jwt.extractEmail(token)).isEqualTo(user.getUsername());
            context.withPropertyValues("jwt.secret=" + key((byte) 9)).run(other ->
                    assertThat(other.getBean(JwtUtil.class).isValid(token, user)).isFalse());
        });
    }

    @Test void propertiesRequireTheDeploymentKeyWithoutAFallback() throws Exception {
        var props = new Properties();
        try (var in = Files.newInputStream(Path.of("src/main/resources/application.properties"))) { props.load(in); }
        assertThat(props.getProperty("jwt.secret")).isEqualTo("${JWT_SECRET}");
        String compose = Files.readString(Path.of("../docker-compose.yml"));
        assertThat(compose).contains("JWT_SECRET: ${JWT_SECRET:?");
    }
}
