package de.htw_berlin.beMindful;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<ActivityEntry, Long> {
    List<ActivityEntry> findByOwner(String owner);
}