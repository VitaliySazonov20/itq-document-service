package com.itq.document.repository;

import com.itq.document.entity.Registry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistryRepository extends JpaRepository<Registry,Long> {
}
