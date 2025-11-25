package com.vectoria.vectoriaapi.dto;

import java.util.List;

public class ChunkUploadRequest {

    private String documentId;
    private List<String> chunks;

    public ChunkUploadRequest() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getChunks() {
        return chunks;
    }

    public void setChunks(List<String> chunks) {
        this.chunks = chunks;
    }
}
