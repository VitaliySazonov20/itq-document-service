package com.itq.document.service;

import com.itq.document.dto.*;
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
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class DocumentService {


    private final DocumentRepository documentRepository;
    private final HistoryRepository historyRepository;
    private final RegistryRepository registryRepository;

    public DocumentService(DocumentRepository documentRepository, HistoryRepository historyRepository, RegistryRepository registryRepository){
        this.documentRepository = documentRepository;
        this.historyRepository = historyRepository;
        this.registryRepository = registryRepository;
    }

    @Transactional
    public Document createDocument(DocumentCreateRequest documentCreateRequest){
        log.info("Creating document");
        long startTime = System.currentTimeMillis();
        Document document = Document.fromRequest(documentCreateRequest);
        document.setUpdatedAt(document.getCreatedAt());
        documentRepository.save(document);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Document created in {}ms", duration);
        History history = new History();
        history.setDocument(document);
        history.setAction(Action.CREATE);
        history.setComment("Document created");
        history.setInitiatedBy(documentCreateRequest.getInitiator());
        historyRepository.save(history);
        return document;
    }

    @Transactional
    public Document getDocument(UUID id){
        Document document = documentRepository.findById(id).orElseThrow();
        List<History> historyList = document.getHistoryList();
        return document;
    }

    @Transactional
    public Page<Document> getPageableDocumentsByIds(List<UUID> uuidList, Pageable pageable){
        if (uuidList == null || uuidList.isEmpty()) {
            throw new ValidationException("Document IDs list cannot be empty");
        }
        return documentRepository.findByIdIn(uuidList,pageable);
    }

    @Transactional
    public List<UUID> getAllDocumentIds(){
        return documentRepository.findAllIds();
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

    public DocumentSubmitResponse submitDocuments(DocumentSubmitRequest request){
        Map<UUID, SubmissionResult> results = new HashMap<>();

        long startTime = System.currentTimeMillis();
        log.info("Starting to process {} documents for submission",request.getDocumentIds().size());
        for(UUID id : request.getDocumentIds()){
            try{
                SubmissionResult result = processSingleDocumentForSubmission(id, request.getInitiator());
                results.put(id,result);
                log.info("Successfully submitted document {}", id);
            } catch (Exception e){
                results.put(id,SubmissionResult.ERROR);
                log.error("Could not submit document {} : {}", id, e.getMessage());
            }
        }
        long duration = System.currentTimeMillis() - startTime;
        log.info("Finished processing {} documents in {}ms", request.getDocumentIds().size(), duration);

        DocumentSubmitResponse response = new DocumentSubmitResponse();
        response.setResults(results);

        return response;
    }

    @Transactional
    public ApproveResult processSingleDocumentForApproval(UUID id, String initiator){
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

        documentRepository.save(document);
        historyRepository.save(history);
        registryRepository.save(registry);

        return ApproveResult.SUCCESS;
    }

    public DocumentApproveResponse approveDocuments(DocumentApproveRequest request){
        Map<UUID, ApproveResult> results = new HashMap<>();

        long startTime = System.currentTimeMillis();
        log.info("Starting to process {} documents for approval",request.getDocumentIds().size());

        for(UUID id : request.getDocumentIds()){
            try{
                ApproveResult result = processSingleDocumentForApproval(id, request.getInitiator());
                results.put(id,result);
                log.info("Successfully approved document {}", id);
            } catch (Exception e){
                results.put(id,ApproveResult.REGISTRY_ERROR);
                log.error("Could not approve document {} : {}", id, e.getMessage());
            }
        }
        long duration = System.currentTimeMillis() - startTime;
        log.info("Finished processing {} documents in {}ms", request.getDocumentIds().size(), duration);

        DocumentApproveResponse response = new DocumentApproveResponse();
        response.setResults(results);

        return response;
    }

    @Transactional
    public Page<Document> searchDocuments(Status status, String author,
                                          LocalDate fromDate, LocalDate toDate,
                                          Pageable pageable){
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (fromDate != null) {
            fromDateTime = fromDate.atStartOfDay();  // 2026-02-20 00:00:00
        }

        if (toDate != null) {
            toDateTime = toDate.atTime(23, 59, 59);  // 2026-02-20 23:59:59
        }

        return documentRepository.searchDocuments(status,author,fromDateTime,toDateTime,pageable);
    }

    public ConcurrentTestResponse runConcurrentApprovalTest(UUID documentId, int threads, int attempts){
        if (threads > attempts) {
            throw new ValidationException("Threads cannot exceed number of attempts");
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<ApproveResult>> futures = new ArrayList<>();
        for (int i = 0; i < attempts; i++){
            futures.add(executorService.submit(()->
                    processSingleDocumentForApproval(documentId, "concurrent-test-user")
            ));
        }

        Map<ApproveResult, Integer> resultCounts = new HashMap<>();

        for (Future<ApproveResult> future: futures){
            try {
                ApproveResult result = future.get();
                resultCounts.merge(result,1,Integer::sum);
            } catch (Exception e) {
                resultCounts.merge(ApproveResult.REGISTRY_ERROR,1,Integer::sum);
            }
        }
        executorService.shutdown();

        Document document = documentRepository.findById(documentId).orElse(null);
        boolean hasRegistry = registryRepository.findByDocumentId(documentId).isPresent();

        ConcurrentTestResponse response = new ConcurrentTestResponse();
        response.setDocumentId(documentId);
        response.setTotalAttempts(attempts);
        response.setConflicted(resultCounts.getOrDefault(ApproveResult.CONFLICT,0));
        response.setSuccessful(resultCounts.getOrDefault(ApproveResult.SUCCESS,0));
        response.setRegistryErrors(resultCounts.getOrDefault(ApproveResult.REGISTRY_ERROR,0));
        response.setNotFound(resultCounts.getOrDefault(ApproveResult.NOT_FOUND,0));

        response.setFinalStatus(document !=null? document.getStatus() : null);
        response.setHasRegistryEntry(hasRegistry);

        return response;
    }

}
