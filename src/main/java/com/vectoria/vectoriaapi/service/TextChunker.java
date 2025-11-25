package com.vectoria.vectoriaapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextChunker {

    /**
     * Teilt den Text in überlappende Chunks auf Basis von Wörtern.
     *
     * @param text          Eingabetext
     * @param maxWords      maximale Anzahl Wörter pro Chunk
     * @param overlapWords  Anzahl der überlappenden Wörter zwischen zwei Chunks
     * @return Liste von Chunk-Strings
     */
    public List<String> chunkByWords(String text, int maxWords, int overlapWords) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        if (maxWords <= 0) {
            throw new IllegalArgumentException("maxWords must be > 0");
        }
        if (overlapWords < 0 || overlapWords >= maxWords) {
            throw new IllegalArgumentException("overlapWords must be >= 0 and < maxWords");
        }

        String[] words = text.trim().split("\\s+");
        List<String> chunks = new ArrayList<>();

        int start = 0;
        while (start < words.length) {
            int end = Math.min(start + maxWords, words.length);

            String chunk = String.join(" ", Arrays.copyOfRange(words, start, end));
            chunks.add(chunk);

            if (end == words.length) {
                break;
            }

            // nächster Chunk beginnt maxWords - overlapWords vorher
            start = end - overlapWords;
        }

        return chunks;
    }
}
