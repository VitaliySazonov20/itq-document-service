package com.itq.document.service;

import com.itq.document.entity.Document;
import com.itq.document.entity.Enum.Action;
import com.itq.document.entity.Enum.ApproveResult;
import com.itq.document.entity.Enum.Status;
import com.itq.document.entity.Enum.SubmissionResult;
import com.itq.document.entity.History;
import com.itq.document.entity.Registry;
import com.itq.document.repository.DocumentRepository;
import com.itq.document.repository.HistoryRepository;
import com.itq.document.repository.RegistryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DocumentHelperService {

    private final DocumentRepository documentRepository;
    private final HistoryRepository historyRepository;
    private final RegistryRepository registryRepository;

    public DocumentHelperService(DocumentRepository documentRepository, HistoryRepository historyRepository, RegistryRepository registryRepository) {
        this.documentRepository = documentRepository;
        this.historyRepository = historyRepository;
        this.registryRepository = registryRepository;
    }

    @Transactional
    public ApproveResult processSingleDocumentForApproval(UUID id, String initiator){
        try {
            Document document = documentRepository.findById(id).orElse(null);
            if(document == null){
                return ApproveResult.NOT_FOUND;
            }
            if (document.getStatus() != Status.SUBMITTED){
                return ApproveResult.CONFLICT;
            }

            document.setStatus(Status.APPROVED);

            History history = new History();
            history.setDocument(document);
            history.setAction(Action.APPROVE);
            history.setInitiatedBy(initiator);
            history.setComment("Document approved");

            Registry registry = new Registry();
            registry.setDocument(document);
            registry.setApprovedBy(initiator);
            registry.setApprovedAt(LocalDateTime.now());

            registryRepository.save(registry);
            historyRepository.save(history);
            documentRepository.save(document);

            return ApproveResult.SUCCESS;
        }
        catch (Exception e) {
            return ApproveResult.REGISTRY_ERROR;
        }
    }

    @Transactional
    public SubmissionResult processSingleDocumentForSubmission(UUID id, String initiator){
        Document document = documentRepository.findById(id).orElse(null);
        if(document == null){
            return SubmissionResult.NOT_FOUND;
        }
        if (document.getStatus() != Status.DRAFT){
            return SubmissionResult.CONFLICT;
        }

        document.setStatus(Status.SUBMITTED);
        History history = new History();
        history.setDocument(document);
        history.setAction(Action.SUBMIT);
        history.setInitiatedBy(initiator);
        history.setComment("Document submitted for approval");

        documentRepository.save(document);
        historyRepository.save(history);

        return SubmissionResult.SUCCESS;
    }

}
