package com.beproject.wordleapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beproject.wordleapi.domain.entity.ERole;
import com.beproject.wordleapi.domain.entity.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}