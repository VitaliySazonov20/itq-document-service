package com.itq.document.dto;

import com.itq.document.entity.Document;
import com.itq.document.entity.Enum.Action;
import com.itq.document.entity.History;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class HistoryDTO {

    private Long id;

    private Action action;

    private String comment;

    private String initiatedBy;

    private LocalDateTime createdAt;

}
