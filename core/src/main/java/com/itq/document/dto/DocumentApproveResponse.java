package com.itq.document.dto;

import com.itq.document.entity.Enum.ApproveResult;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class DocumentApproveResponse {
    private Map<UUID, ApproveResult> results;
}
