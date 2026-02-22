package com.itq.document.service;

import com.itq.document.entity.Document;
import com.itq.document.entity.Enum.ApproveResult;
import com.itq.document.entity.Enum.Status;
import com.itq.document.repository.DocumentRepository;
import com.itq.document.repository.HistoryRepository;
import com.itq.document.repository.RegistryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private RegistryRepository registryRepository;

    @InjectMocks
    private DocumentService documentService;

    private UUID documentId;
    private Document submittedDocument;

    @BeforeEach
    void setUp() {
        documentId = UUID.randomUUID();

        submittedDocument = new Document();
        submittedDocument.setId(documentId);
        submittedDocument.setUniqueNumber("DOC-TEST-123");
        submittedDocument.setAuthor("Test Author");
        submittedDocument.setTitle("Test Title");
        submittedDocument.setStatus(Status.SUBMITTED);
        submittedDocument.setCreatedAt(LocalDateTime.now());
        submittedDocument.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void approve_RollbackOnRegistryError() {
        when(documentRepository.findById(documentId))
                .thenReturn(Optional.of(submittedDocument));

        when(registryRepository.save(any()))
                .thenThrow(new RuntimeException("Database error"));

        ApproveResult result = documentService.processSingleDocumentForApproval(
                documentId, "Test Initiator");

        assertEquals(ApproveResult.REGISTRY_ERROR, result);
//        assertEquals(Status.SUBMITTED, submittedDocument.getStatus());

        verify(documentRepository).findById(documentId);
        verify(documentRepository, never()).save(any());
        verify(historyRepository, never()).save(any());
        verify(registryRepository).save(any());
    }
}