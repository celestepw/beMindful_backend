package de.htw_berlin.beMindful;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivityController {

    @GetMapping("/activities")
    public List<ActivityEntry> getAllActivities() {
        return List.of(
                new ActivityEntry("Breathing Exercise"),
                new ActivityEntry("Journaling"),
                new ActivityEntry("Meditation")
        );
    }

}