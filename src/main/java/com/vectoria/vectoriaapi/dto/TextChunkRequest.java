package com.vectoria.vectoriaapi.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TextChunkRequest {

    private String documentId;
    private String text;
    private Integer maxWords;
    private Integer overlapWords;

    public TextChunkRequest() {
    }

}
