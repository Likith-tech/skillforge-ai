package com.skillforge;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Shared base for any test that needs a real Postgres (repository tests, full
 * @SpringBootTest controller/integration tests). @ServiceConnection wires the
 * container's JDBC URL/credentials into the Spring context automatically -
 * no manual @DynamicPropertySource needed. One container is started per test
 * class that extends this (not reused across classes) since enabling
 * Testcontainers' cross-class reuse requires machine-level opt-in config this
 * repo shouldn't assume is present in every environment (including CI).
 */
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
}
