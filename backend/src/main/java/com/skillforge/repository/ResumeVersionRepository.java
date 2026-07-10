package com.skillforge.repository;

import com.skillforge.model.ResumeVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, Long> {

    List<ResumeVersion> findByStudentIdOrderByVersionNumberDesc(Long studentId);

    Optional<ResumeVersion> findTopByStudentIdOrderByVersionNumberDesc(Long studentId);
}
