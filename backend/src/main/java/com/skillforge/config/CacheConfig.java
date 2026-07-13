package com.skillforge.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's cache abstraction. No explicit CacheManager bean is declared,
 * so Spring Boot auto-configures a ConcurrentMapCacheManager - an in-memory,
 * per-instance cache. That's adequate for the one cached read today
 * (SkillRepository.findAll, see its @Cacheable) but won't stay consistent
 * across multiple app instances; a horizontally-scaled deployment would need a
 * shared cache (Redis) instead.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
