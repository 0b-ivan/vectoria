# Vectoria – Postgres + pgvector mit Docker

Diese README beschreibt **das Docker-Setup** für eine PostgreSQL-Datenbank mit der `pgvector`-Extension,  für das RAG-Projekt verwendest.

---

## 📦 Inhalt des Dockerfiles

Das `Dockerfile` basiert auf dem offiziellen PostgreSQL-Image und installiert darin die `pgvector`-Extension:

- Basis-Image: `postgres:16.4`
- Installiert Build-Dependencies und Postgres-Dev-Pakete
- Klont das `pgvector`-Repository und kompiliert/installiert die Extension
- Entfernt Build-Tools wieder, damit das Image schlank bleibt

---

## 🔧 Voraussetzungen

- Installiertes **Docker**
- Optional: **docker-compose** (falls du später erweitern willst)
- Freier Port `5432` auf deinem Host

---

## 🏗️ Image bauen

Im Projektverzeichnis, in dem sich dein `Dockerfile` befindet:

```bash
docker build -t vectoria-postgres-pgvector .
