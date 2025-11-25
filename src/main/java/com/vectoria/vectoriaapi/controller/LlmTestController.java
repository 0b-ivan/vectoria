package com.vectoria.vectoriaapi.controller;

import com.vectoria.vectoriaapi.service.LlmClient;
import org.springframework.web.bind.annotation.*;

@RestController
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

    public static class ChatRequest {
        private String prompt;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }

    public static class ChatResponse {
        private String answer;

        public ChatResponse(String answer) {
            this.answer = answer;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
