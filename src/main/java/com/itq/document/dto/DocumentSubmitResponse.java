package com.itq.document.dto;

import com.itq.document.entity.Enum.SubmissionResult;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class DocumentSubmitResponse {

    private Map<UUID, SubmissionResult> results;
}
