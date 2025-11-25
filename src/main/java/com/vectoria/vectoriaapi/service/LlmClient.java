package com.vectoria.vectoriaapi.service;

import com.vectoria.vectoriaapi.config.LlmClientConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LlmClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final LlmClientConfig config;

    public LlmClient(LlmClientConfig config) {
        this.config = config;
    }

    // 🚩 NEUE chat-METHODE: nimmt einen String, kein List<Map<...>>
    public String chat(String userMessage) {
        String url = config.getBaseUrl() + "/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getChatModel());

        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", userMessage)
        );
        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (config.getApiKey() != null && !config.getApiKey().isBlank()) {
            headers.set("Authorization", "Bearer " + config.getApiKey());
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new IllegalStateException("Empty response from LLM");
        }

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) responseBody.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("No choices in response");
        }

        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }

    // 🔹 NEU: Embeddings abrufen
    public List<Double> embed(String text) {
        // 🔹 Einfaches lokales "Fake"-Embedding:
        // - feste Dimension (z.B. 256)
        // - wir hashen Tokens in diese Dimension
        // - zählt Vorkommen pro Bucket
        // → deterministisch, für Tests ausreichend

        int dim = 256;
        double[] vec = new double[dim];

        if (text == null || text.isBlank()) {
            List<Double> empty = new ArrayList<>(dim);
            for (int i = 0; i < dim; i++) empty.add(0.0);
            return empty;
        }

        String[] tokens = text
                .toLowerCase()
                .replaceAll("[^a-z0-9äöüß]+", " ")
                .trim()
                .split("\\s+");

        for (String token : tokens) {
            if (token.isBlank()) continue;
            int h = token.hashCode() & 0x7fffffff;
            int idx = h % dim;
            vec[idx] += 1.0;
        }

        // Normierung (L2), damit Cosine-Similarity sinnvoll ist
        double norm = 0.0;
        for (double v : vec) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        if (norm == 0.0) norm = 1.0;

        List<Double> result = new ArrayList<>(dim);
        for (double v : vec) {
            result.add(v / norm);
        }
        return result;
    }

}
