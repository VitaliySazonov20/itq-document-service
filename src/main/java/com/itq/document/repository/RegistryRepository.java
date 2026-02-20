package com.itq.document.repository;

import com.itq.document.entity.Registry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistryRepository extends JpaRepository<Registry,Long> {
    public Optional<Registry> findByDocumentId(UUID id);
}
