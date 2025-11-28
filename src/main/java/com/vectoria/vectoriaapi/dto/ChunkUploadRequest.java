package com.vectoria.vectoriaapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ChunkUploadRequest {

    private String documentId;
    private List<String> chunks;

    public ChunkUploadRequest() {
    }

}
