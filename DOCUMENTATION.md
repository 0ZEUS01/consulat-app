\# Consulat-App — Project Documentation



End-to-end full-stack project simulating a Consular Request Tracker (Spring Boot, Angular, PostgreSQL, Docker, CI/CD, Kubernetes).



\## Phase 1: Project Structure \& Git Setup



\### Folder Architecture

\- `consulat-app/` (Monorepo root)

&#x20; - `backend/` — Spring Boot REST API

&#x20; - `frontend/` — Angular web application

&#x20; - `k8s/` — Kubernetes manifests (planned)

&#x20; - `.github/workflows/` — GitHub Actions CI/CD pipelines (planned)

&#x20; - `.gitignore` — Excludes build outputs (`target/`, `node\_modules/`, `.env`)



\### Git Commands Used

1\. Initialize repository:

&#x20;  ```bash

&#x20;  git init

