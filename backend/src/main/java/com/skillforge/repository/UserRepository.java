package com.skillforge.repository;

import com.skillforge.model.RoleName;
import com.skillforge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRoles_Name(RoleName name);
}
