package com.vectoria.vectoriaapi.repository;

import com.vectoria.vectoriaapi.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    // Liefert alle Chunks eines Dokuments in Reihenfolge
    List<DocumentChunk> findByDocumentIdOrderByChunkIndex(String documentId);
}
