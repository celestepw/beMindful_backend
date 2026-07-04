package de.htw_berlin.beMindful.auth;

import de.htw_berlin.beMindful.security.JwtUtil;
import de.htw_berlin.beMindful.user.User;
import de.htw_berlin.beMindful.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-Tests für den AuthController. Die per @Autowired injizierten Felder
 * werden über Reflection mit Mocks belegt (kein Spring-Kontext nötig).
 */
class AuthControllerTest {

    private final UserRepository userRepo = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final AuthController controller = new AuthController();

    {
        ReflectionTestUtils.setField(controller, "userRepo", userRepo);
        ReflectionTestUtils.setField(controller, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(controller, "jwtUtil", jwtUtil);
    }

    private RegisterRequest registerReq(String username, String email, String password) {
        RegisterRequest req = new RegisterRequest();
        req.setUsername(username);
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    private LoginRequest loginReq(String username, String password) {
        LoginRequest req = new LoginRequest();
        req.setUsername(username);
        req.setPassword(password);
        return req;
    }

    @Test
    void register_savesUser_whenUsernameFree() {
        when(userRepo.findByUsername("celeste")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("geheim")).thenReturn("HASH");

        ResponseEntity<?> res = controller.register(registerReq("celeste", "c@example.com", "geheim"));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userRepo).save(argThat(u ->
                u.getUsername().equals("celeste")
                        && u.getEmail().equals("c@example.com")
                        && u.getPasswordHash().equals("HASH")));
    }

    @Test
    void register_rejects_whenUsernameTaken() {
        when(userRepo.findByUsername("celeste")).thenReturn(Optional.of(new User()));

        ResponseEntity<?> res = controller.register(registerReq("celeste", "c@example.com", "geheim"));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userRepo, never()).save(any());
    }

    @Test
    void login_returnsToken_whenCredentialsCorrect() {
        User user = new User();
        user.setUsername("celeste");
        user.setPasswordHash("HASH");
        when(userRepo.findByUsername("celeste")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("geheim", "HASH")).thenReturn(true);
        when(jwtUtil.generateToken("celeste")).thenReturn("JWT123");

        ResponseEntity<?> res = controller.login(loginReq("celeste", "geheim"));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(Map.of("token", "JWT123"));
    }

    @Test
    void login_returns401_whenPasswordWrong() {
        User user = new User();
        user.setUsername("celeste");
        user.setPasswordHash("HASH");
        when(userRepo.findByUsername("celeste")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("falsch", "HASH")).thenReturn(false);

        ResponseEntity<?> res = controller.login(loginReq("celeste", "falsch"));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_returns401_whenUserUnknown() {
        when(userRepo.findByUsername("niemand")).thenReturn(Optional.empty());

        ResponseEntity<?> res = controller.login(loginReq("niemand", "egal"));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
