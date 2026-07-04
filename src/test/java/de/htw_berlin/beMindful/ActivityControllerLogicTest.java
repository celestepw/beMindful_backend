package de.htw_berlin.beMindful;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Reine Unit-Tests für die Geschäftslogik im ActivityController (ohne Spring-Kontext).
 * Repository und Authentication werden gemockt.
 */
class ActivityControllerLogicTest {

    private final ActivityRepository repository = mock(ActivityRepository.class);
    private final ActivityController controller = new ActivityController(repository);
    private final Authentication auth = mock(Authentication.class);

    private ActivityEntry entryOn(LocalDate date) {
        ActivityEntry e = new ActivityEntry();
        e.setDate(date);
        return e;
    }

    private void loggedInAs(String username) {
        when(auth.getName()).thenReturn(username);
    }

    @Test
    void streak_isZero_whenNoEntries() {
        loggedInAs("celeste");
        when(repository.findByOwner("celeste")).thenReturn(List.of());

        assertThat(controller.getStreak(auth)).isZero();
    }

    @Test
    void streak_countsConsecutiveDaysIncludingToday() {
        loggedInAs("celeste");
        LocalDate today = LocalDate.now();
        when(repository.findByOwner("celeste")).thenReturn(List.of(
                entryOn(today), entryOn(today.minusDays(1)), entryOn(today.minusDays(2))));

        assertThat(controller.getStreak(auth)).isEqualTo(3);
    }

    @Test
    void streak_startsFromYesterday_whenTodayMissing() {
        loggedInAs("celeste");
        LocalDate today = LocalDate.now();
        when(repository.findByOwner("celeste")).thenReturn(List.of(
                entryOn(today.minusDays(1)), entryOn(today.minusDays(2))));

        assertThat(controller.getStreak(auth)).isEqualTo(2);
    }

    @Test
    void streak_isZero_whenGapBeforeToday() {
        loggedInAs("celeste");
        LocalDate today = LocalDate.now();
        when(repository.findByOwner("celeste")).thenReturn(List.of(entryOn(today.minusDays(3))));

        assertThat(controller.getStreak(auth)).isZero();
    }

    @Test
    void streak_stopsAtFirstGap() {
        loggedInAs("celeste");
        LocalDate today = LocalDate.now();
        // heute + gestern zählen (2), danach Lücke, ältere Einträge werden ignoriert
        when(repository.findByOwner("celeste")).thenReturn(List.of(
                entryOn(today), entryOn(today.minusDays(1)), entryOn(today.minusDays(5))));

        assertThat(controller.getStreak(auth)).isEqualTo(2);
    }

    @Test
    void getAllActivities_returnsOnlyOwnEntries() {
        loggedInAs("celeste");
        when(repository.findByOwner("celeste")).thenReturn(List.of(new ActivityEntry("Atemübung")));

        List<ActivityEntry> result = controller.getAllActivities(auth);

        assertThat(result).hasSize(1);
    }

    @Test
    void addActivity_setsOwnerAndDate() {
        loggedInAs("celeste");
        when(repository.save(any(ActivityEntry.class))).thenAnswer(i -> i.getArgument(0));

        ActivityEntry saved = controller.addActivity(new ActivityEntry("Atemübung"), auth);

        assertThat(saved.getOwner()).isEqualTo("celeste");
        assertThat(saved.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void updateActivity_throwsForbidden_whenNotOwner() {
        loggedInAs("celeste");
        ActivityEntry existing = new ActivityEntry();
        existing.setOwner("jemandAnders");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> controller.updateActivity(1L, new ActivityEntry(), auth))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteActivity_throwsNotFound_whenMissing() {
        loggedInAs("celeste");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.deleteActivity(99L, auth))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }
}
