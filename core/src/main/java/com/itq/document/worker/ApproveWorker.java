package com.itq.document.worker;

import com.itq.document.entity.Document;
import com.itq.document.entity.Enum.Status;
import com.itq.document.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ApproveWorker {

    private final DocumentService documentService;

    private final int batchSize;

    public ApproveWorker(DocumentService documentService,
                         @Value("${worker.batch-size:5}") int batchSize) {
        this.documentService = documentService;
        this.batchSize = batchSize;
    }

    @Scheduled(cron = "${worker.approve-cron:0 */5 * * * *}")
    private void processDraftDocuments(){

        Pageable pageable = PageRequest.of(0,batchSize);
        Page<Document> documentPage = documentService.searchDocuments(
                Status.SUBMITTED,
                null,
                null,
                null,
                pageable);

        log.info("Starting approval worker with batchSize: {}", batchSize);
        int processed = 0;
        List<Document> documentList =documentPage.getContent();
        for(Document doc: documentList){
            documentService.processSingleDocumentForApproval(doc.getId(),
                    "Approval Worker");
            processed++;
            log.info("Progress: {}/{} documents processed for approval ({}%)",processed,documentList.size(),
                    processed*100/documentList.size());
        }
    }
}
