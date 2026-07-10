package com.skillforge.repository;

import com.skillforge.model.JobLocation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLocationRepository extends JpaRepository<JobLocation, Long> {

    Optional<JobLocation> findByDisplayNameIgnoreCase(String displayName);
}