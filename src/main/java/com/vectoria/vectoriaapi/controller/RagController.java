package com.vectoria.vectoriaapi.controller;

import com.vectoria.vectoriaapi.service.EmbeddingService;
import com.vectoria.vectoriaapi.service.RagService;
import com.vectoria.vectoriaapi.service.RagService.RagAnswer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin
public class RagController {

    private final EmbeddingService embeddingService;
    private final RagService ragService;

    public RagController(EmbeddingService embeddingService, RagService ragService) {
        this.embeddingService = embeddingService;
        this.ragService = ragService;
    }

    @PostMapping("/embed/{documentId}")
    public EmbedResponse embedDocument(@PathVariable String documentId) {
        int updated = embeddingService.embedAllChunksForDocument(documentId);
        EmbedResponse resp = new EmbedResponse();
        resp.setDocumentId(documentId);
        resp.setEmbeddedChunks(updated);
        return resp;
    }

    @PostMapping("/ask")
    public RagAnswer ask(@RequestBody RagRequest request) {
        int topK = request.getTopK() != null ? request.getTopK() : 5;
        return ragService.answerQuestion(request.getDocumentId(), request.getQuestion(), topK);
    }



    public static class RagRequest {
        private String documentId;
        private String question;
        private Integer topK;

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public Integer getTopK() {
            return topK;
        }

        public void setTopK(Integer topK) {
            this.topK = topK;
        }
    }

    public static class EmbedResponse {
        private String documentId;
        private int embeddedChunks;

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public int getEmbeddedChunks() {
            return embeddedChunks;
        }

        public void setEmbeddedChunks(int embeddedChunks) {
            this.embeddedChunks = embeddedChunks;
        }
    }
}
