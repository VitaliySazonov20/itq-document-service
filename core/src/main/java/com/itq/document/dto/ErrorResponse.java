package com.itq.document.dto;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {

    private String code;
    private String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
