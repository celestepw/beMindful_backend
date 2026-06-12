package de.htw_berlin.beMindful;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "https://bemindful-frontend.onrender.com")
public class ActivityController {

    private final ActivityRepository repository;

    public ActivityController(ActivityRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/activities")
    public List<ActivityEntry> getAllActivities() {
        return repository.findAll();
    }

    @PostMapping("/activities")
    public ActivityEntry addActivity(@RequestBody ActivityEntry entry) {
        entry.setDate(LocalDate.now());
        return repository.save(entry);
    }

    @GetMapping("/streak")
    public long getStreak() {
        Set<LocalDate> days = repository.findAll().stream()
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
}
