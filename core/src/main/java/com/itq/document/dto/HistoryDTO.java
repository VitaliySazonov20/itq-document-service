package com.itq.document.dto;

import com.itq.document.entity.Enum.Action;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryDTO {

    private Long id;

    private Action action;

    private String comment;

    private String initiatedBy;

    private LocalDateTime createdAt;

}
