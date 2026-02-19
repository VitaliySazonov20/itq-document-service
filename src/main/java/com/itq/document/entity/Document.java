package com.itq.document.entity;

import com.itq.document.dto.DocumentCreateRequest;
import com.itq.document.entity.Enum.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "unique_number", nullable = false, unique = true)
    private String uniqueNumber;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private Status status = Status.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at" , nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" , nullable = false)
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<History> historyList = new ArrayList<>();

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private Registry registry;

    private String generateUniqueNumber(){
        return "DOC-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

    public static Document fromRequest(DocumentCreateRequest request){
        Document document = new Document();
        document.setAuthor(request.getAuthor());
        document.setTitle(request.getTitle());
        document.setUniqueNumber(document.generateUniqueNumber());
        return document;
    }

}
