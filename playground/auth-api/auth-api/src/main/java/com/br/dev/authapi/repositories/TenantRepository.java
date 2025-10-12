package com.br.dev.authapi.repositories;

import com.br.dev.authapi.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByName(String name);
    Optional<Tenant> findById(Long id);
}

