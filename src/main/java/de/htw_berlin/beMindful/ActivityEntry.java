package de.htw_berlin.beMindful;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class ActivityEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;     // z.B. "Atemübung" oder "Journal"
    private String mood;      // Stimmung beim Journaling
    private boolean done;
    private LocalDate date;    // Tag, an dem erledigt – fürs Streak

    public ActivityEntry() {}

    public ActivityEntry(String title) {
        this.title = title;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}