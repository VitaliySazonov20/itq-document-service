package com.itq.document.restController;

import com.itq.document.dto.DocumentCreateRequest;
import com.itq.document.dto.DocumentCreationResponse;
import com.itq.document.dto.DocumentGetResponse;
import com.itq.document.entity.Document;
import com.itq.document.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<DocumentCreationResponse> createDocument(@RequestBody DocumentCreateRequest request){
        DocumentCreationResponse documentCreationResponse =
                DocumentCreationResponse.fromDocument(documentService.createDocument(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(documentCreationResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentGetResponse> getDocument(@PathVariable UUID id){
        DocumentGetResponse response = DocumentGetResponse
                .fromDocument(documentService.getDocument(id));
        return ResponseEntity.ok(response);
    }


}
