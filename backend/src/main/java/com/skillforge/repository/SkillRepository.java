package com.skillforge.repository;

import com.skillforge.model.Skill;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCase(String name);

    /**
     * The full catalog is re-read on every resume upload and every ATS analysis
     * (skill extraction scans resume text against it), but it only changes when a
     * job/resume mentions a brand-new skill name or at startup seeding - a classic
     * read-heavy, write-rare table. Cached here rather than at the call sites so
     * every caller (ResumeServiceImpl, the ATS scoring services) benefits without
     * each needing its own cache-aware wrapper.
     */
    @Cacheable("skillCatalog")
    @Override
    List<Skill> findAll();

    @CacheEvict(value = "skillCatalog", allEntries = true)
    @Override
    <S extends Skill> S save(S entity);
}
