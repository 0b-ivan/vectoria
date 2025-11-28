package com.vectoria.vectoriaapi.controller;

import com.vectoria.vectoriaapi.service.LlmClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/llm")
@CrossOrigin
public class LlmTestController {

    private final LlmClient llmClient;

    public LlmTestController(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String prompt = request.getPrompt();

        // Hier kommt der String vom LLM zurück
        String answer = llmClient.chat(prompt);

        return new ChatResponse(answer);
    }

    @Setter
    @Getter
    public static class ChatRequest {
        private String prompt;

    }

    @Setter
    @Getter
    public static class ChatResponse {
        private String answer;

        public ChatResponse(String answer) {
            this.answer = answer;
        }

    }
}
