package com.itq.document.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "registry")
public class Registry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "document_id", nullable = false, unique = true, insertable = false, updatable = false)
//    private UUID documentId;

    @OneToOne
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    @Column(name = "approved_at", nullable = false, updatable = false)
    private LocalDateTime approvedAt;

    @Column(name = "approved_by", nullable = false)
    private String approvedBy;
}
