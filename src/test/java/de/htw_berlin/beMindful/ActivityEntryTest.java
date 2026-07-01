package de.htw_berlin.beMindful;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reiner Unit-Test ohne Spring-Kontext: prüft, dass die Felder der Entität
 * korrekt gesetzt und ausgelesen werden.
 */
class ActivityEntryTest {

    @Test
    void settersAndGetters_workCorrectly() {
        ActivityEntry entry = new ActivityEntry();

        LocalDate date = LocalDate.of(2026, 6, 30);
        entry.setId(42L);
        entry.setTitle("Journal");
        entry.setMood("gut");
        entry.setDone(true);
        entry.setDate(date);
        entry.setOwner("celeste");
        entry.setNote("Heute war ein guter Tag.");

        assertEquals(42L, entry.getId());
        assertEquals("Journal", entry.getTitle());
        assertEquals("gut", entry.getMood());
        assertTrue(entry.isDone());
        assertEquals(date, entry.getDate());
        assertEquals("celeste", entry.getOwner());
        assertEquals("Heute war ein guter Tag.", entry.getNote());
    }

    @Test
    void constructorWithTitle_setsTitle() {
        ActivityEntry entry = new ActivityEntry("Atemübung");

        assertEquals("Atemübung", entry.getTitle());
    }
}
