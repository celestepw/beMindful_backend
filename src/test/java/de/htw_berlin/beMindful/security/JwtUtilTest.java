package de.htw_berlin.beMindful.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit-Tests für JwtUtil. Das @Value-Feld "secret" wird per Reflection gesetzt,
 * damit kein Spring-Kontext gebraucht wird.
 */
class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "test-secret-key-fuer-junit-mindestens-32-bytes-lang-1234567890");
    }

    @Test
    void generatedToken_isValid_andExtractsUsername() {
        String token = jwtUtil.generateToken("celeste");

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("celeste");
    }
}
