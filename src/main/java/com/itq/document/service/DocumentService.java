package com.itq.document.service;

import com.itq.document.dto.DocumentCreateRequest;
import com.itq.document.entity.Document;
import com.itq.document.entity.Enum.Action;
import com.itq.document.entity.History;
import com.itq.document.repository.DocumentRepository;
import com.itq.document.repository.HistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    public List<UUID> getAllDocumentIds(){
        return documentRepository.findAllIds();

    }

}
