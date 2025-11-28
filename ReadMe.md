# Vectoria – Local RAG System with Java Spring + llama (WIP)

Vectoria ist ein vollständig lokales **Retrieval-Augmented-Generation System (RAG)**, implementiert in  
**Java Spring Boot** mit einem **lokalen LLM über llama.cpp** gesteuert via **LiteLLM**.

Das System ermöglicht:

- Upload von Dokumenten
- automatisches Chunking
- Generierung von Embeddings über Chat (kein /embeddings-Endpoint!)
- semantische Suche per Cosine Similarity
- Anfragebeantwortung durch RAG-Pipeline
- schlankes Frontend mit Dokumentauswahl + Fragestellung

---

## 🚀 Features

- 🟢 **100% lokal** – keine Cloud, keine externen APIs
- 🧩 **Chat-basierte Embeddings** (Qwen/llama.cpp → LiteLLM → /chat/completions)
- 📚 **Dokument-Upload & Chunking**
- 🔎 **Similarity Search** mit Cosine Similarity über Postgres JSONB
- 🤖 **RAG Prompt Generation** für hochwertige Antworten
- 🌐 Frontend für Benutzerabfragen
- 🔧 Erweiterbar, modular & klar strukturiert

---

## 🏗 Architekturüberblick

### Backend (Spring Boot)

| Komponente        | Beschreibung |
|------------------|--------------|
| **ChunkController** | Upload, Chunking, Dokumentliste |
| **EmbeddingService** | Generiert Embeddings für Dokument-Chunks via Chat |
| **RagService** | Frage-Embedding, Similarity Search, Prompt-Erstellung, LLM-Query |
| **LlmClient** | `/chat/completions` API; sowohl Chat-Antworten als auch Embeddings via Chat |
| **PostgreSQL** | Persistiert Chunks + deren Embeddings (JSONB) |

### LLM Layer

Vectoria nutzt *lokale Modelle* wie Qwen oder LLaMA über **llama.cpp**.  
LiteLLM dient als **OpenAI-kompatible Chat-API**, jedoch nur für `/chat/completions`.

**Keine Verwendung von `/embeddings`!**

Embeddings werden so generiert: -> WIP

Das Ergebnis wird geparst und als `embedding_json` gespeichert.

---

## 🗄 Datenbank-Schema

### `document_chunks`

```sql
CREATE TABLE document_chunks (
    id BIGSERIAL PRIMARY KEY,
    document_id TEXT NOT NULL,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    embedding_json JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

## LiteLLM Konfiguration (erforderlich)
litellm-config:
```yaml
model_list:
  - model_name: qwen-local-chat
    litellm_params:
      # Modellname aus Sicht von LiteLLM – kann irgendwas sein
      model: gpt-3.5-turbo
      # llama.cpp-Server (OpenAI-kompatible API)
      api_base: "http://llama:8000/v1"
      api_key: "dummy"
      # WICHTIG: KEIN custom_llm_provider hier

  - model_name: qwen-local-embed
    litellm_params:
      model: gpt-3.5-turbo
      api_base: "http://llama:8000/v1"
      api_key: "dummy"
```

## RAG Ablauf
1.	Benutzer stellt Frage
2.	Frage wird eingebettet (Chat-Hack)
3.	DB lädt alle Chunk-Embeddings
4.	Cosine Similarity → Top-K Chunks
5.	Prompt wird mit Kontext gebaut
6.	LLM antwortet via LlmClient.chat()
7.	Antwort und verwendete Chunks werden zurückgegeben

## Backend starten

```bash
mvn spring-boot:run
```

