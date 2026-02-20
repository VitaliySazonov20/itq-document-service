package com.itq.document.restController;

import com.itq.document.dto.ConcurrentTestRequest;
import com.itq.document.dto.ConcurrentTestResponse;
import com.itq.document.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConcurrencyTestController {

    private final DocumentService documentService;

    public ConcurrencyTestController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/test/concurrent-approve")
    public ResponseEntity<ConcurrentTestResponse> testConcurrentApproval(
            @RequestBody @Valid ConcurrentTestRequest request){

        ConcurrentTestResponse response = documentService.runConcurrentApprovalTest(request.getDocumentId(),
                request.getThreads(), request.getAttempts());

        return ResponseEntity.ok(response);
    }
}
