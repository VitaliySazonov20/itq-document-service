package com.itq.document.repository;

import com.itq.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Page<Document> findByIdIn(List<UUID> ids, Pageable pageable);

    @Query("SELECT d.id FROM Document d")
    List<UUID> findAllIds();

}
