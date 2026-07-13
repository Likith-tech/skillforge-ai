package com.skillforge.repository;

import com.skillforge.model.RoleSkillRequirement;
import com.skillforge.model.TargetRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleSkillRequirementRepository extends JpaRepository<RoleSkillRequirement, Long> {

    List<RoleSkillRequirement> findByRole(TargetRole role);

    boolean existsByRole(TargetRole role);
}
