package com.itq.document.repository;

import com.itq.document.entity.Document;
import com.itq.document.entity.Enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Page<Document> findByIdIn(List<UUID> ids, Pageable pageable);

    @Query("SELECT d.id FROM Document d")
    List<UUID> findAllIds();


    @Query("SELECT d from Document d WHERE " +
        "(:status IS NULL OR d.status = :status) AND " +
        "(:author IS NULL OR d.author = :author) AND " +
        "(COALESCE(:fromDate,'') = '' OR d.createdAt >= CAST(:fromDate AS timestamp)) AND "+
        "(COALESCE(:toDate,'') = '' OR d.createdAt <= CAST(:toDate AS timestamp))")
    Page<Document> searchDocuments(
            @Param("status")Status status,
            @Param("author") String author,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate")LocalDateTime toDate,
            Pageable pageable);

}