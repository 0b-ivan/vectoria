package com.vectoria.vectoriaapi.dto;

public class ChunkUploadResponse {

    private String documentId;
    private int chunksStored;

    public ChunkUploadResponse(String documentId, int chunksStored) {
        this.documentId = documentId;
        this.chunksStored = chunksStored;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getChunksStored() {
        return chunksStored;
    }
}
