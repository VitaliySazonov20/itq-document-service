package com.itq.document.service;

import com.itq.document.dto.DocumentCreateRequest;
import com.itq.document.dto.DocumentSubmitRequest;
import com.itq.document.dto.DocumentSubmitResponse;
import com.itq.document.entity.Document;
import com.itq.document.entity.Enum.Action;
import com.itq.document.entity.Enum.Status;
import com.itq.document.entity.Enum.SubmissionResult;
import com.itq.document.entity.History;
import com.itq.document.repository.DocumentRepository;
import com.itq.document.repository.HistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final HistoryRepository historyRepository;

    public DocumentService(DocumentRepository documentRepository, HistoryRepository historyRepository){
        this.documentRepository = documentRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public Document createDocument(DocumentCreateRequest documentCreateRequest){
        Document document = Document.fromRequest(documentCreateRequest);
        document.setUpdatedAt(document.getCreatedAt());
        documentRepository.save(document);
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
        return documentRepository.findByIdIn(uuidList,pageable);
    }

    @Transactional
    public List<UUID> getAllDocumentIds(){
        return documentRepository.findAllIds();
    }

    @Transactional
    public SubmissionResult processSingleDocument(UUID id, String initiator){
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

        for(UUID id : request.getDocumentIds()){
            try{
                SubmissionResult result = processSingleDocument(id, request.getInitiator());
                results.put(id,result);
            } catch (Exception e){
                results.put(id,SubmissionResult.ERROR);
            }
        }

        DocumentSubmitResponse response = new DocumentSubmitResponse();
        response.setResults(results);
        return response;
    }

}
