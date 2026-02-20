package com.itq.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DocumentSubmitRequest {

    @NotEmpty(message = "Documents IDs List cannot be empty")
    @Size(max = 1000, message = "Maximum 1000 document IDs allowed")
    private List<UUID> documentIds;

    @NotBlank(message = "Initiator is required")
    private String initiator;
}
