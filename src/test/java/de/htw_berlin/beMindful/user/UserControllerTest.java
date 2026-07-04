package de.htw_berlin.beMindful.user;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-Tests für den UserController (Passwort ändern). Repository, PasswordEncoder
 * und Authentication werden gemockt.
 */
class UserControllerTest {

    private final UserRepository userRepo = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserController controller = new UserController(userRepo, passwordEncoder);
    private final Authentication auth = mock(Authentication.class);

    private User existingUser(String hash) {
        User user = new User();
        user.setUsername("celeste");
        user.setPasswordHash(hash);
        when(auth.getName()).thenReturn("celeste");
        when(userRepo.findByUsername("celeste")).thenReturn(Optional.of(user));
        return user;
    }

    private PasswordChangeRequest changeReq(String current, String next) {
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.setCurrentPassword(current);
        req.setNewPassword(next);
        return req;
    }

    @Test
    void changePassword_succeeds_whenCurrentCorrectAndNewValid() {
        User user = existingUser("OLD");
        when(passwordEncoder.matches("altesPw", "OLD")).thenReturn(true);
        when(passwordEncoder.encode("neuesPw123")).thenReturn("NEWHASH");

        ResponseEntity<?> res = controller.changePassword(changeReq("altesPw", "neuesPw123"), auth);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(user.getPasswordHash()).isEqualTo("NEWHASH");
        verify(userRepo).save(user);
    }

    @Test
    void changePassword_returns400_whenCurrentPasswordWrong() {
        existingUser("OLD");
        when(passwordEncoder.matches("falsch", "OLD")).thenReturn(false);

        ResponseEntity<?> res = controller.changePassword(changeReq("falsch", "neuesPw123"), auth);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userRepo, never()).save(any());
    }

    @Test
    void changePassword_returns400_whenNewPasswordTooShort() {
        existingUser("OLD");
        when(passwordEncoder.matches("altesPw", "OLD")).thenReturn(true);

        ResponseEntity<?> res = controller.changePassword(changeReq("altesPw", "123"), auth);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userRepo, never()).save(any());
    }
}
