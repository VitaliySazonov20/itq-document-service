package com.itq.document.controller;


import com.itq.document.dto.DocumentApproveRequest;
import com.itq.document.dto.DocumentCreateRequest;
import com.itq.document.dto.DocumentSubmitRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void createAndGetDocume_Success() throws Exception{

        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setAuthor("Test Author");
        request.setTitle("Test Title");
        request.setInitiator("Test Initiator");

        String createRequestJson = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.uniqueNumber").exists())
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseJson);
        String documentId = jsonNode.get("id").asString();

        mockMvc.perform(get("/api/documents/{id}", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(documentId))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.historyList").isArray())
                .andExpect(jsonPath("$.historyList", hasSize(greaterThanOrEqualTo(1))));


    }


    @Test
    public void batchSubmit_PartialSuccess() throws Exception {

        DocumentCreateRequest draftRequest = new DocumentCreateRequest();
        draftRequest.setAuthor("Draft Author");
        draftRequest.setTitle("Draft Title");
        draftRequest.setInitiator("Test Initiator");

        String draftJson = objectMapper.writeValueAsString(draftRequest);

        MvcResult draftResult = mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(draftJson))
                .andExpect(status().isCreated())
                .andReturn();

        String draftResponse = draftResult.getResponse().getContentAsString();
        JsonNode draftNode = objectMapper.readTree(draftResponse);
        String draftId = draftNode.get("id").asText();

        DocumentCreateRequest submittedRequest = new DocumentCreateRequest();
        submittedRequest.setAuthor("Submitted Author");
        submittedRequest.setTitle("Submitted Title");
        submittedRequest.setInitiator("Test Initiator");

        String submittedJson = objectMapper.writeValueAsString(submittedRequest);

        MvcResult submittedResult = mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submittedJson))
                .andExpect(status().isCreated())
                .andReturn();

        String submittedResponse = submittedResult.getResponse().getContentAsString();
        JsonNode submittedNode = objectMapper.readTree(submittedResponse);
        String submittedId = submittedNode.get("id").asText();

        // Submit this document first to change its status
        DocumentSubmitRequest submitFirstRequest = new DocumentSubmitRequest();
        submitFirstRequest.setDocumentIds(List.of(UUID.fromString(submittedId)));
        submitFirstRequest.setInitiator("Test Initiator");

        String submitFirstJson = objectMapper.writeValueAsString(submitFirstRequest);

        mockMvc.perform(post("/api/documents/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitFirstJson))
                .andExpect(status().isOk());

        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        DocumentSubmitRequest batchRequest = new DocumentSubmitRequest();
        batchRequest.setDocumentIds(List.of(
                UUID.fromString(draftId),
                UUID.fromString(submittedId),
                UUID.fromString(nonExistentId)
        ));
        batchRequest.setInitiator("Batch Test Initiator");

        String batchRequestJson = objectMapper.writeValueAsString(batchRequest);

        mockMvc.perform(post("/api/documents/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(batchRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results['%s']".formatted(draftId)).value("SUCCESS"))
                .andExpect(jsonPath("$.results['%s']".formatted(submittedId)).value("CONFLICT"))
                .andExpect(jsonPath("$.results['%s']".formatted(nonExistentId)).value("NOT_FOUND"));
    }

    @Test
    public void batchApprove_PartialSuccess() throws Exception {
        DocumentCreateRequest createRequest = new DocumentCreateRequest();
        createRequest.setAuthor("Approve Author");
        createRequest.setTitle("Approve Title");
        createRequest.setInitiator("Test Initiator");

        String createJson = objectMapper.writeValueAsString(createRequest);

        MvcResult createResult = mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createNode = objectMapper.readTree(createResponse);
        String documentId = createNode.get("id").asText();

        DocumentSubmitRequest submitRequest = new DocumentSubmitRequest();
        submitRequest.setDocumentIds(List.of(UUID.fromString(documentId)));
        submitRequest.setInitiator("Test Initiator");

        String submitJson = objectMapper.writeValueAsString(submitRequest);

        mockMvc.perform(post("/api/documents/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitJson))
                .andExpect(status().isOk());

        DocumentCreateRequest createRequest2 = new DocumentCreateRequest();
        createRequest2.setAuthor("Approve Author 2");
        createRequest2.setTitle("Approve Title 2");
        createRequest2.setInitiator("Test Initiator");

        String createJson2 = objectMapper.writeValueAsString(createRequest2);

        MvcResult createResult2 = mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson2))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse2 = createResult2.getResponse().getContentAsString();
        JsonNode createNode2 = objectMapper.readTree(createResponse2);
        String documentId2 = createNode2.get("id").asText();

        DocumentSubmitRequest submitRequest2 = new DocumentSubmitRequest();
        submitRequest2.setDocumentIds(List.of(UUID.fromString(documentId2)));
        submitRequest2.setInitiator("Test Initiator");

        String submitJson2 = objectMapper.writeValueAsString(submitRequest2);

        mockMvc.perform(post("/api/documents/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitJson2))
                .andExpect(status().isOk());

        DocumentCreateRequest draftRequest = new DocumentCreateRequest();
        draftRequest.setAuthor("Draft Author");
        draftRequest.setTitle("Draft Title");
        draftRequest.setInitiator("Test Initiator");

        String draftJson = objectMapper.writeValueAsString(draftRequest);

        MvcResult draftResult = mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(draftJson))
                .andExpect(status().isCreated())
                .andReturn();

        String draftResponse = draftResult.getResponse().getContentAsString();
        JsonNode draftNode = objectMapper.readTree(draftResponse);
        String draftId = draftNode.get("id").asText();

        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        DocumentApproveRequest batchRequest = new DocumentApproveRequest();
        batchRequest.setDocumentIds(List.of(
                UUID.fromString(documentId),     // Should succeed
                UUID.fromString(documentId2),    // Should succeed
                UUID.fromString(draftId),        // Should conflict (not SUBMITTED)
                UUID.fromString(nonExistentId)   // Should be NOT_FOUND
        ));
        batchRequest.setInitiator("Batch Approve Initiator");

        String batchRequestJson = objectMapper.writeValueAsString(batchRequest);

        mockMvc.perform(post("/api/documents/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(batchRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results['%s']".formatted(documentId)).value("SUCCESS"))
                .andExpect(jsonPath("$.results['%s']".formatted(documentId2)).value("SUCCESS"))
                .andExpect(jsonPath("$.results['%s']".formatted(draftId)).value("CONFLICT"))
                .andExpect(jsonPath("$.results['%s']".formatted(nonExistentId)).value("NOT_FOUND"));

        mockMvc.perform(get("/api/documents/{id}", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(get("/api/documents/{id}", documentId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(get("/api/documents/{id}", draftId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

}
