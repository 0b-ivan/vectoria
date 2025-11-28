package com.vectoria.vectoriaapi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "vectoria.llm")
public class LlmClientConfig {

    private String baseUrl;
    private String apiKey;
    private String embeddingModel;
    private String chatModel;

    // getters / setters

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
    }
}
