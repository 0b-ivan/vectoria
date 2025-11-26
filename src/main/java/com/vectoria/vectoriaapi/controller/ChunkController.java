package com.vectoria.vectoriaapi.controller;

import com.vectoria.vectoriaapi.dto.ChunkUploadRequest;
import com.vectoria.vectoriaapi.dto.ChunkUploadResponse;
import com.vectoria.vectoriaapi.model.DocumentChunk;
import com.vectoria.vectoriaapi.repository.DocumentChunkRepository;
import com.vectoria.vectoriaapi.service.EmbeddingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import com.vectoria.vectoriaapi.dto.TextChunkRequest;
import com.vectoria.vectoriaapi.service.ChunkService;

@RestController
@RequestMapping("/api/chunks")
public class ChunkController {

    private final DocumentChunkRepository repository;
    private final ChunkService chunkService;
    private final EmbeddingService embeddingService;

    public ChunkController(DocumentChunkRepository repository, ChunkService chunkService, EmbeddingService embeddingService) {
        this.repository = repository;
        this.chunkService = chunkService;
        this.embeddingService = embeddingService;

    }

    /**
     * Nimmt documentId + Liste von Chunk-Strings entgegen
     * und speichert sie mit fortlaufendem chunk_index.
     *
     * POST /api/chunks
     */
    @PostMapping
    public ResponseEntity<ChunkUploadResponse> uploadChunks(
            @RequestBody ChunkUploadRequest request
    ) {
        if (request.getDocumentId() == null || request.getDocumentId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getChunks() == null || request.getChunks().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String documentId = request.getDocumentId();
        List<String> chunkTexts = request.getChunks();

        List<DocumentChunk> toSave = new ArrayList<>();

        for (int i = 0; i < chunkTexts.size(); i++) {
            String content = chunkTexts.get(i);
            if (content == null || content.isBlank()) {
                continue; // leere Chunks überspringen
            }
            DocumentChunk chunk = new DocumentChunk(documentId, i, content);
            toSave.add(chunk);
        }

        repository.saveAll(toSave);

        ChunkUploadResponse response =
                new ChunkUploadResponse(documentId, toSave.size());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/chunks/from-text
     * Nimmt rohen Text entgegen, chunked ihn und speichert alle Chunks in der DB.
     */
    @PostMapping("/from-text")
    public ResponseEntity<ChunkUploadResponse> uploadFromText(
            @RequestBody TextChunkRequest request
    ) {
        if (request.getDocumentId() == null || request.getDocumentId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getText() == null || request.getText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        int maxWords = (request.getMaxWords() != null) ? request.getMaxWords() : 300;
        int overlapWords = (request.getOverlapWords() != null) ? request.getOverlapWords() : 50;

        // 1) Chunks speichern
        var savedChunks = chunkService.chunkAndStore(
                request.getDocumentId(),
                request.getText(),
                maxWords,
                overlapWords
        );

        // 2) Embeddings für alle Chunks erzeugen
        int embeddedCount = embeddingService.embedAllChunksForDocument(request.getDocumentId());

        // Optional: DTO erweitern, z.B. noch embeddedCount zurückgeben
        ChunkUploadResponse response =
                new ChunkUploadResponse(request.getDocumentId(), savedChunks.size());
        // response.setEmbeddedCount(embeddedCount);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /**
     * Holt alle Chunks eines Dokuments in Reihenfolge.
     *
     * GET /api/chunks/{documentId}
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<List<DocumentChunk>> getChunks(
            @PathVariable String documentId
    ) {
        List<DocumentChunk> chunks =
                repository.findByDocumentIdOrderByChunkIndex(documentId);

        if (chunks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(chunks);
    }

    @GetMapping("/documents")
    public List<String> getAllDocumentIds() {
        return repository.findAll()
                .stream()
                .map(DocumentChunk::getDocumentId)
                .distinct()
                .toList();
    }
}
