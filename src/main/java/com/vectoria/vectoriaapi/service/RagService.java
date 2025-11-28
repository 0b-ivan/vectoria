package com.vectoria.vectoriaapi.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vectoria.vectoriaapi.model.DocumentChunk;
import com.vectoria.vectoriaapi.repository.DocumentChunkRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final DocumentChunkRepository chunkRepository;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public RagService(DocumentChunkRepository chunkRepository,
                      LlmClient llmClient,
                      ObjectMapper objectMapper) {
        this.chunkRepository = chunkRepository;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    public RagAnswer answerQuestion(String documentId, String question, int topK) {
        List<DocumentChunk> chunks =
                chunkRepository.findByDocumentIdOrderByChunkIndex(documentId);

        if (chunks.isEmpty()) {
            RagAnswer ra = new RagAnswer();
            ra.setDocumentId(documentId);
            ra.setQuestion(question);
            ra.setAnswer("Für dieses Dokument wurden noch keine Chunks gefunden. Bitte zuerst Text hochladen und embedden.");
            ra.setUsedChunks(List.of());
            return ra;
        }

        // Embedding für die Frage
        List<Double> queryEmbedding = llmClient.embed(question);

        // Ähnlichkeit zu allen Chunks berechnen
        List<ScoredChunk> scored = new ArrayList<>();

        for (DocumentChunk chunk : chunks) {
            if (chunk.getEmbeddingJson() == null || chunk.getEmbeddingJson().isBlank()) {
                // optional: hier on-the-fly Embedding -> WIP
                continue;
            }

            try {
                List<Double> chunkEmbedding =
                        objectMapper.readValue(chunk.getEmbeddingJson(), new TypeReference<>() {});

                double score = cosineSimilarity(queryEmbedding, chunkEmbedding);
                scored.add(new ScoredChunk(chunk, score));
            } catch (Exception e) {
                //Logger Bauen -> WIP
            }
        }

        if (scored.isEmpty()) {
            throw new IllegalStateException("Keine Chunks mit Embeddings gefunden.");
        }

        // Top-K nach Score
        scored.sort((a, b) -> Double.compare(b.score, a.score));
        List<ScoredChunk> top = scored.stream()
                .limit(topK)
                .collect(Collectors.toList());

        // Kontext-Prompt bauen
        StringBuilder contextBuilder = new StringBuilder();
        for (ScoredChunk sc : top) {
            contextBuilder
                    .append("Chunk #").append(sc.chunk.getChunkIndex())
                    .append(" (Score=").append(String.format(Locale.US, "%.3f", sc.score)).append(")\n")
                    .append(sc.chunk.getContent())
                    .append("\n\n");
        }

        String prompt = """
                Du bist ein Assistent, der Fragen zu einem Dokument beantwortet.

                Verwende ausschließlich die folgenden Chunks als Wissensbasis:

                %s

                Frage: %s

                Antworte präzise auf Deutsch und erwähne nicht explizit, dass du Chunks benutzt hast.
                """.formatted(contextBuilder.toString(), question);

        String answer = llmClient.chat(prompt);

        // DTO für die Antwort
        RagAnswer ragAnswer = new RagAnswer();
        ragAnswer.setDocumentId(documentId);
        ragAnswer.setQuestion(question);
        ragAnswer.setAnswer(answer);

        List<RagAnswer.UsedChunk> used = new ArrayList<>();
        for (ScoredChunk sc : top) {
            RagAnswer.UsedChunk uc = new RagAnswer.UsedChunk();
            uc.setChunkIndex(sc.chunk.getChunkIndex());
            uc.setScore(sc.score);
            uc.setPreview(shorten(sc.chunk.getContent(), 200));
            used.add(uc);
        }
        ragAnswer.setUsedChunks(used);

        return ragAnswer;
    }

    private String shorten(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + "…";
    }

    private double cosineSimilarity(List<Double> a, List<Double> b) {
        int n = Math.min(a.size(), b.size());
        if (n == 0) return 0.0;

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < n; i++) {
            double va = a.get(i);
            double vb = b.get(i);
            dot += va * vb;
            normA += va * va;
            normB += vb * vb;
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private record ScoredChunk(DocumentChunk chunk, double score) {}

    @Setter
    @Getter
    public static class RagAnswer {
        private String documentId;
        private String question;
        private String answer;
        private List<UsedChunk> usedChunks;

        @Setter
        @Getter
        public static class UsedChunk {
            private String documentId;
            private int chunkIndex;
            private double score;
            private String preview;

        }
    }
}
