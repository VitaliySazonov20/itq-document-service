package com.itq.document.entity;

import com.itq.document.entity.Enum.Action;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


//    @Column(name = "document_id" , nullable = false, insertable = false, updatable = false)
//    private UUID documentId;
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Enumerated(EnumType.STRING)
    @Column(name = "action" , nullable = false)
    private Action action;

    @Column(name = "comment")
    private String comment;

    @Column(name = "initiated_by", nullable = false)
    private String initiatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
