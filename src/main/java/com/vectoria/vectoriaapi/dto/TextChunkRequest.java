package com.vectoria.vectoriaapi.dto;

public class TextChunkRequest {

    private String documentId;
    private String text;
    private Integer maxWords;
    private Integer overlapWords;

    public TextChunkRequest() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getMaxWords() {
        return maxWords;
    }

    public void setMaxWords(Integer maxWords) {
        this.maxWords = maxWords;
    }

    public Integer getOverlapWords() {
        return overlapWords;
    }

    public void setOverlapWords(Integer overlapWords) {
        this.overlapWords = overlapWords;
    }
}
