
package com.vectoria.vectoriaapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "document_id", nullable = false)
    private String documentId;

    @Getter
    @Setter
    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Getter
    @Setter
    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Getter
    @Setter
    @Column(name = "embedding_json", columnDefinition = "text")
    private String embeddingJson;

    @Getter
    @Setter
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public DocumentChunk() {}

    public DocumentChunk(String documentId, Integer chunkIndex, String content) {
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.createdAt = OffsetDateTime.now();
    }

    // Getter / Setter ...
}
