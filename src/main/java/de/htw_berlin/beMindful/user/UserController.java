package de.htw_berlin.beMindful.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"https://bemindful-frontend.onrender.com", "http://localhost:5173"})
public class UserController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Konto-Infos des angemeldeten Nutzers
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        User user = userRepo.findByUsername(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }

    // Passwort ändern (aktuelles Passwort wird geprüft)
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest req, Authentication authentication) {
        User user = userRepo.findByUsername(authentication.getName()).orElseThrow();

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(400).body("Aktuelles Passwort ist falsch");
        }
        if (req.getNewPassword() == null || req.getNewPassword().length() < 6) {
            return ResponseEntity.status(400).body("Neues Passwort muss mindestens 6 Zeichen haben");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("Passwort geändert");
    }
}
