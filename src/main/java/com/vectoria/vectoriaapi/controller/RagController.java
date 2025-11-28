package com.vectoria.vectoriaapi.controller;

import com.vectoria.vectoriaapi.service.EmbeddingService;
import com.vectoria.vectoriaapi.service.RagService;
import com.vectoria.vectoriaapi.service.RagService.RagAnswer;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;


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



    @Setter
    @Getter
    public static class RagRequest {
        private String documentId;
        private String question;
        private Integer topK;

    }

    @Setter
    @Getter
    public static class EmbedResponse {
        private String documentId;
        private int embeddedChunks;

    }
}
