package de.htw_berlin.beMindful;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"https://bemindful-frontend.onrender.com", "http://localhost:5173"})
public class ActivityController {

    private final ActivityRepository repository;

    public ActivityController(ActivityRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/activities")
    public List<ActivityEntry> getAllActivities(Authentication authentication) {
        String username = authentication.getName();
        return repository.findByOwner(username);
    }

    @PostMapping("/activities")
    public ActivityEntry addActivity(@RequestBody ActivityEntry entry, Authentication authentication) {
        entry.setDate(LocalDate.now());
        entry.setOwner(authentication.getName());
        return repository.save(entry);
    }

    @GetMapping("/streak")
    public long getStreak(Authentication authentication) {
        String username = authentication.getName();
        List<ActivityEntry> entries = repository.findByOwner(username);

        Set<LocalDate> days = entries.stream()
                .map(ActivityEntry::getDate)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (days.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate cursor;

        if (days.contains(today)) {
            cursor = today;
        } else if (days.contains(today.minusDays(1))) {
            cursor = today.minusDays(1);
        } else {
            return 0;
        }

        long streak = 0;
        while (days.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    @PutMapping("/activities/{id}")
    public ActivityEntry updateActivity(@PathVariable Long id, @RequestBody ActivityEntry update, Authentication authentication) {
        ActivityEntry entry = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        if (!entry.getOwner().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your activity");
        }

        entry.setTitle(update.getTitle());
        entry.setMood(update.getMood());
        entry.setDone(update.isDone());
        if (update.getNote() != null) {
            entry.setNote(update.getNote());
        }
        return repository.save(entry);
    }

    @DeleteMapping("/activities/{id}")
    public void deleteActivity(@PathVariable Long id, Authentication authentication) {
        ActivityEntry entry = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        if (!entry.getOwner().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your activity");
        }

        repository.deleteById(id);
    }
}