package de.htw_berlin.beMindful.auth;

import de.htw_berlin.beMindful.security.JwtUtil;
import de.htw_berlin.beMindful.user.User;
import de.htw_berlin.beMindful.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"https://bemindful-frontend.onrender.com", "http://localhost:5173"})
public class AuthController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username bereits vergeben");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("Registriert");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> userOpt = userRepo.findByUsername(req.getUsername());
        if (userOpt.isEmpty() || !passwordEncoder.matches(req.getPassword(), userOpt.get().getPasswordHash())) {
            return ResponseEntity.status(401).body("Falsche Zugangsdaten");
        }
        String token = jwtUtil.generateToken(userOpt.get().getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }
}