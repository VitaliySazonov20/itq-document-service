package com.itq.document.dto;

import com.itq.document.entity.Document;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DocumentGetResponse {

    private UUID id;

    private String uniqueNumber;

    private String author;

    private String title;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<HistoryDTO> historyList;


    public static DocumentGetResponse fromDocument(Document document){
        DocumentGetResponse response = new DocumentGetResponse();
        response.setId(document.getId());
        response.setUniqueNumber(document.getUniqueNumber());
        response.setAuthor(document.getAuthor());
        response.setTitle(document.getTitle());
        response.setStatus(document.getStatus().toString());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());

        List<HistoryDTO> historyDTOList =document.getHistoryList().stream()
                .map(history -> {
                    HistoryDTO dto =new HistoryDTO();
                    dto.setId(history.getId());
                    dto.setAction(history.getAction());
                    dto.setComment(history.getComment());
                    dto.setInitiatedBy(history.getInitiatedBy());
                    dto.setCreatedAt(history.getCreatedAt());
                    return dto;
                })
                .toList();
        response.setHistoryList(historyDTOList);

        return response;
    }
}
