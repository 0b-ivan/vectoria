package com.vectoria.vectoriaapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vectoria.vectoriaapi.model.DocumentChunk;
import com.vectoria.vectoriaapi.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    private final LlmClient llmClient;
    private final DocumentChunkRepository chunkRepository;
    private final ObjectMapper objectMapper;

    public EmbeddingService(LlmClient llmClient,
                            DocumentChunkRepository chunkRepository,
                            ObjectMapper objectMapper) {
        this.llmClient = llmClient;
        this.chunkRepository = chunkRepository;
        this.objectMapper = objectMapper;
    }

    /**
     *
     * Embedding function -> temporary really stupid
     *
     * @return Zero or size of Chunks ti embedding
     */

    @Transactional
    public int embedAllChunksForDocument(String documentId) {
        List<DocumentChunk> chunks =
                chunkRepository.findByDocumentIdOrderByChunkIndex(documentId);

        if (chunks.isEmpty()) {
            return 0;
        }

        List<DocumentChunk> toUpdate = new ArrayList<>();

        for (DocumentChunk chunk : chunks) {
            if (chunk.getEmbeddingJson() != null && !chunk.getEmbeddingJson().isBlank()) {
                continue; // schon eingebettet
            }

            List<Double> embedding = llmClient.embed(chunk.getContent());
            try {
                String json = objectMapper.writeValueAsString(embedding);
                chunk.setEmbeddingJson(json);
                toUpdate.add(chunk);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize embedding to JSON", e);
            }
        }

        if (!toUpdate.isEmpty()) {
            chunkRepository.saveAll(toUpdate);
        }

        return toUpdate.size();
    }
}
