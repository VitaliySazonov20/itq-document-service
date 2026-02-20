package com.itq.document.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ConcurrentTestRequest {

    @NotNull
    private UUID documentId;

    @Min(2)
    private int threads =5;

    @Min(2)
    private int attempts = 10;

}
