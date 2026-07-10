package com.skillforge.repository;

import com.skillforge.model.RoadmapStep;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, Long> {

    List<RoadmapStep> findByRoadmapIdOrderByStepOrderAsc(Long roadmapId);
}