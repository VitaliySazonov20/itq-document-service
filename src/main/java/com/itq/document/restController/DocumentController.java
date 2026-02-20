package com.itq.document.restController;

import com.itq.document.dto.DocumentBatchResponse;
import com.itq.document.dto.DocumentCreateRequest;
import com.itq.document.dto.DocumentCreationResponse;
import com.itq.document.dto.DocumentGetResponse;
import com.itq.document.entity.Document;
import com.itq.document.service.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping
    public ResponseEntity<Page<DocumentBatchResponse>> getDocuments(
            @RequestParam("ids") List<UUID> uuidList,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){

        Pageable pageable = PageRequest.of(page,size);
        Page<Document> documentPage = documentService.getPageableDocumentsByIds(uuidList,pageable);
        Page<DocumentBatchResponse> response = documentPage
                .map(DocumentBatchResponse::fromDocument);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ids")
    public ResponseEntity<List<UUID>> getAllDocumentIds(){
        List<UUID> uuids = documentService.getAllDocumentIds();
        return ResponseEntity.ok(uuids);
    }

//    @PostMapping("/submit")
//    public ResponseEntity<?> submitAllDocuments(@RequestBody){}
}
