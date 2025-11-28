package com.vectoria.vectoriaapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "document_id", nullable = false)
    private String documentId;

    @Setter
    @Column(name = "chunk_index", nullable = false)
    private int chunkIndex;

    @Setter
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Setter
    @Column(name = "embedding_json")
    private String embeddingJson;

    @Setter
    @Column(name = "created_at")
    private Instant createdAt;

    public DocumentChunk() {
    }

    public DocumentChunk(String documentId, int chunkIndex, String content) {
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.createdAt = Instant.now();
    }
}
