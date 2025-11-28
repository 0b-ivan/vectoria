CREATE TABLE document_chunks (
  id BIGSERIAL PRIMARY KEY,
  document_id TEXT NOT NULL,
  chunk_index INT NOT NULL,
  content TEXT NOT NULL,
  embedding_json TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_document_chunks_document_id
  ON document_chunks(document_id);

CREATE INDEX idx_document_chunks_doc_chunk
  ON document_chunks(document_id, chunk_index);