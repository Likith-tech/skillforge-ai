package com.skillforge.repository;

import com.skillforge.model.PlacementScore;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementScoreRepository extends JpaRepository<PlacementScore, Long> {

    Optional<PlacementScore> findTopByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<PlacementScore> findTop6ByStudentIdOrderByCreatedAtDesc(Long studentId);
}