package com.vectoria.vectoriaapi.service;

import com.vectoria.vectoriaapi.model.DocumentChunk;
import com.vectoria.vectoriaapi.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkService {

    private final DocumentChunkRepository repository;
    private final TextChunker textChunker;

    public ChunkService(DocumentChunkRepository repository) {
        this.repository = repository;
        this.textChunker = new TextChunker();
    }

    /**
     * Nur chunken, ohne etwas zu speichern.
     */
    public List<String> chunkText(String text, int maxWords, int overlapWords) {
        return textChunker.chunkByWords(text, maxWords, overlapWords);
    }

    /**
     * Text chunken und alle Chunks als DocumentChunk in der DB speichern.
     *
     * @return gespeicherte Chunks
     */
    public List<DocumentChunk> chunkAndStore(String documentId, String text, int maxWords, int overlapWords) {
        List<String> chunks = textChunker.chunkByWords(text, maxWords, overlapWords);
        List<DocumentChunk> entities = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            String content = chunks.get(i);
            DocumentChunk chunk = new DocumentChunk(documentId, i, content);
            entities.add(chunk);
        }

        return repository.saveAll(entities);
    }
}
