package com.vectoria.vectoriaapi.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChunkUploadResponse {

    private String documentId;
    private int chunksStored;

    public ChunkUploadResponse(String documentId, int chunksStored) {
        this.documentId = documentId;
        this.chunksStored = chunksStored;
    }

}
