package com.itq.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentCreateRequest {

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Initiator is required")
    private String initiator;
}
