package com.itq.utility.dto;

import lombok.Data;

@Data
public class DocumentCreateRequest {

    private String author;
    private String title;
    private String initiator;
}
