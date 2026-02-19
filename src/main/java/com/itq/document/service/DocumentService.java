package com.itq.document.service;

import com.itq.document.dto.DocumentCreateRequest;
import com.itq.document.entity.Document;
import com.itq.document.entity.Enum.Action;
import com.itq.document.entity.History;
import com.itq.document.repository.DocumentRepository;
import com.itq.document.repository.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        documentRepository.save(document);
        History history = new History();
        history.setDocument(document);
        history.setAction(Action.CREATE);
        history.setComment("Document created");
        history.setInitiatedBy(documentCreateRequest.getInitiator());
        historyRepository.save(history);
        return document;
    }
}
