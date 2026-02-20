package com.itq.document.dto;


import com.itq.document.entity.Enum.Status;
import lombok.Data;

import java.util.UUID;

@Data
public class ConcurrentTestResponse {

    private UUID documentId;
    private int totalAttempts;
    private int successful;
    private int conflicted;
    private int notFound;
    private int registryErrors;
    private Status finalStatus;
    private boolean hasRegistryEntry;
}
