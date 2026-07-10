package com.skillforge.repository;

import com.skillforge.model.PlacementTimelineEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementTimelineEntryRepository extends JpaRepository<PlacementTimelineEntry, Long> {

    List<PlacementTimelineEntry> findByStudentIdOrderByOccurredAtDesc(Long studentId);
}