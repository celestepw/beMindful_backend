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

    @Test
    void isTokenValid_returnsFalse_forGarbageToken() {
        assertThat(jwtUtil.isTokenValid("kein-echter-token")).isFalse();
    }

    @Test
    void isTokenValid_returnsFalse_whenSignedWithDifferentSecret() {
        JwtUtil other = new JwtUtil();
        ReflectionTestUtils.setField(other, "secret",
                "ein-voellig-anderes-secret-mit-mindestens-32-bytes-xyz");
        String foreignToken = other.generateToken("celeste");

        assertThat(jwtUtil.isTokenValid(foreignToken)).isFalse();
    }
}
