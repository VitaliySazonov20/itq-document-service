package com.itq.document.dto;

import com.itq.document.entity.Document;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DocumentBatchResponse {
    private UUID id;

    private String uniqueNumber;

    private String author;

    private String title;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static DocumentBatchResponse fromDocument(Document document){
        DocumentBatchResponse response = new DocumentBatchResponse();
        response.setId(document.getId());
        response.setUniqueNumber(document.getUniqueNumber());
        response.setAuthor(document.getAuthor());
        response.setTitle(document.getTitle());
        response.setStatus(document.getStatus().toString());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());
        return response;
    }

}
