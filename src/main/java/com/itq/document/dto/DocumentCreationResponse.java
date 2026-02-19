package com.itq.document.dto;

import com.itq.document.entity.Document;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DocumentCreationResponse {
    private UUID id;
    private String uniqueNumber;
    private String author;
    private String title;
    private String status;
    private LocalDateTime createdAt;

    public static DocumentCreationResponse fromDocument(Document document){
        DocumentCreationResponse response = new DocumentCreationResponse();
        response.setId(document.getId());
        response.setUniqueNumber(document.getUniqueNumber());
        response.setAuthor(document.getAuthor());
        response.setTitle(document.getTitle());
        response.setStatus(document.getStatus().toString());
        response.setCreatedAt(document.getCreatedAt());
        return response;
    }
}


