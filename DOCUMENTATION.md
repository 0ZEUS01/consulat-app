# Consulat-App — Project Documentation

End-to-end full-stack project simulating a Consular Request Tracker built with **Spring Boot**, **Angular**, **PostgreSQL**, **Docker**, **GitHub Actions (CI/CD)**, and **Kubernetes**.

---

## Phase 1: Project Structure & Git Setup

### Folder Architecture
- `consulat-app/` (Monorepo root)
  - [`backend/`](./backend/) — Spring Boot REST API
  - [`frontend/`](./frontend/) — Angular web application
  - `k8s/` — Kubernetes manifests *(planned)*
  - `.github/workflows/` — GitHub Actions CI/CD pipelines *(planned)*
  - [`.gitignore`](./.gitignore) — Excludes build outputs (`backend/target/`, `frontend/node_modules/`, `frontend/dist/`, `.env`)

### Git Commands Used
1. Initialize the repository, add `.gitignore`, and push to `main`:
   ```bash
   git init
   git add .gitignore
   git commit -m "chore: initial project setup with gitignore"
   git branch -M main
   git remote add origin [https://github.com/](https://github.com/)<USERNAME>/consulat-app.git
   git push -u origin main
   ```
2. Create and switch to a development branch (never code directly on `main`):
   ```bash
   git checkout -b dev
   ```

---

## Phase 2: Backend Development (Spring Boot + PostgreSQL)

### 1. Database & Environment Configuration
- **File:** [`backend/src/main/resources/application.properties`](./backend/src/main/resources/application.properties)
- **Purpose:** Uses environment variables with local fallback defaults so the exact same build works locally, in Docker, and in Kubernetes without code changes.

```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:consulat_db}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:password}

spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

### 2. 3-Tier Architecture Files (`com.example.consulat`)

| Layer | File Location | Role |
| :--- | :--- | :--- |
| **Entity** | [`Demande.java`](./backend/src/main/java/com/example/consulat/entity/Demande.java) | Maps the `demande` PostgreSQL table (`id`, `cin`, `typeDocument`, `statut`). |
| **Repository** | [`DemandeRepository.java`](./backend/src/main/java/com/example/consulat/repository/DemandeRepository.java) | Extends `JpaRepository<Demande, Long>` for automatic SQL CRUD operations. |
| **Service** | [`DemandeService.java`](./backend/src/main/java/com/example/consulat/service/DemandeService.java) | Business logic: lists requests and automatically sets `statut = "EN_ATTENTE"` on creation. |
| **Controller** | [`DemandeController.java`](./backend/src/main/java/com/example/consulat/controller/DemandeController.java) | Exposes REST API endpoints under `/api/demandes` with `@CrossOrigin(origins = "*")`. |

### 3. API Endpoints Implemented
- `GET /api/demandes` — Returns all consular requests.
- `POST /api/demandes` — Creates a new consular request (JSON body: `{"cin": "AB12345", "typeDocument": "Passeport"}`).

### 4. Verification (Postman)
- Started Spring Boot on `http://localhost:8080`.
- Tested `POST /api/demandes` → Returned `200 OK` with auto-generated `id` and `statut: "EN_ATTENTE"`.
- Tested `GET /api/demandes` → Returned JSON array of persisted records from `consulat_db`.

---

## Phase 3: Frontend Development (Angular 19)

### 1. Setup & CLI Generation Commands
```bash
ng new frontend --routing --style=css
cd frontend
ng generate service services/demande
ng generate component components/demande-list
```

### 2. Frontend Files & Locations

| Component / Config | File Location | Role |
| :--- | :--- | :--- |
| **Environment** | [`environment.ts`](./frontend/src/environments/environment.ts) | Externalizes the backend API URL (`http://localhost:8080/api/demandes`). |
| **App Config** | [`app.config.ts`](./frontend/src/app/app.config.ts) | Registers `provideHttpClient(withFetch())` for SSR-compatible HTTP calls. |
| **SSR Bootstrap** | [`main.server.ts`](./frontend/src/main.server.ts) | Passes `BootstrapContext` to `bootstrapApplication` (fixes Angular 19.2+ `NG0401` SSR error). |
| **Model** | [`demande.model.ts`](./frontend/src/app/models/demande.model.ts) | TypeScript interface mirroring the Java `Demande` entity. |
| **Service** | [`demande.service.ts`](./frontend/src/app/services/demande.service.ts) | Consumes `GET` and `POST` endpoints using `HttpClient` and `Observable`. |
| **List Logic** | [`demande-list.component.ts`](./frontend/src/app/components/demande-list/demande-list.component.ts) | Standalone component managing the form state and fetching/refreshing requests. |
| **List View** | [`demande-list.component.html`](./frontend/src/app/components/demande-list/demande-list.component.html) | Renders the creation form and live table of consular requests. |
| **Root Component** | [`app.component.ts`](./frontend/src/app/app.component.ts) & [`app.component.html`](./frontend/src/app/app.component.html) | Imports and displays `<app-demande-list></app-demande-list>`. |

### 3. Troubleshooting Note (Angular 19 SSR Fix)
When running `ng serve` with SSR enabled in Angular 19.2+, `NG0401: Missing Platform` occurred because `bootstrapApplication` lacked a `BootstrapContext`.
- **Fix:** Updated [`main.server.ts`](./frontend/src/main.server.ts) to accept and pass `context: BootstrapContext`, and updated [`app.config.ts`](./frontend/src/app/app.config.ts) to use `provideHttpClient(withFetch())`.

### 4. Verification & Git Milestone
- Ran `ng serve` on `http://localhost:4200`.
- Verified end-to-end flow: submitting the form in Angular saves to PostgreSQL via Spring Boot and updates the table immediately.
- Committed and pushed to the `dev` branch:
  ```bash
  git add .
  git commit -m "feat: implement Demande CRUD backend and Angular frontend"
  git push -u origin dev
  ```

---

## Phase 4: Containerization (Docker & Docker Compose)

### Files Added

| File | Location | Purpose |
| :--- | :--- | :--- |
| **Backend Dockerfile** | [`backend/Dockerfile`](./backend/Dockerfile) | Multi-stage build: compiles Spring Boot with Maven, runs `.jar` on lightweight `eclipse-temurin` JRE Alpine image. |
| **Nginx Config** | [`frontend/nginx.conf`](./frontend/nginx.conf) | Serves compiled Angular static files and handles SPA routing via `try_files`. |
| **Frontend Dockerfile** | [`frontend/Dockerfile`](./frontend/Dockerfile) | Multi-stage build: compiles Angular with Node 22, serves `dist/frontend/browser` using `nginx:alpine`. |
| **Docker Compose** | [`docker-compose.yml`](./docker-compose.yml) | Orchestrates `db` (PostgreSQL 16), `backend` (Spring Boot), and `frontend` (Angular + Nginx) on an isolated bridge network (`consulat-net`). |

### Key Architecture Decisions
- **Multi-Stage Builds:** Keeps production images small and secure by excluding Maven, Node.js, and raw source code from final runtime containers.
- **Data Persistence:** Mounted a named Docker volume (`postgres_data`) to `/var/lib/postgresql/data` so database records survive container restarts and deletions.
- **Environment Injection:** Passed `DB_HOST=db` via `docker-compose.yml`, allowing Spring Boot to connect to the PostgreSQL container without modifying Java code.

### Commands & Verification
```bash
# Build and start all containers in detached mode
docker compose up --build -d

# Verify all 3 containers are running
docker ps

# Inspect backend logs if needed
docker logs consulat-backend

# Test volume persistence
docker compose down
docker compose up -d
```
- Verified `http://localhost:4200` communicates with the containerized backend (`8080`) and persists records across `docker compose down/up`.

---

## Phase 5: CI/CD Pipeline (GitHub Actions & GHCR)

### Files Added

| File | Location | Purpose |
| :--- | :--- | :--- |
| **Backend Unit Test** | [`DemandeServiceTest.java`](./backend/src/test/java/com/example/consulat/DemandeServiceTest.java) | JUnit 5 + Mockito unit test verifying `createDemande()` sets default status `EN_ATTENTE` without needing a live database. |
| **CI/CD Workflow** | [`ci-cd.yml`](./.github/workflows/ci-cd.yml) | Automates testing, building, and pushing Docker images on every push to `dev` or `main`. |

### Pipeline Stages
1. **`backend-ci`:** Sets up JDK 21, executes `DemandeServiceTest`, and compiles the Spring Boot JAR.
2. **`frontend-ci`:** Sets up Node 22, installs dependencies, and runs `npm run build` to verify the Angular compilation.
3. **`docker-push`:** Runs only if both CI jobs succeed (`needs: [backend-ci, frontend-ci]`). Logs into GitHub Container Registry (`ghcr.io`) using `GITHUB_TOKEN`, builds both multi-stage Dockerfiles, and pushes `:latest` images.

### Verification
- Verified unit test locally with `mvn test -Dtest=DemandeServiceTest`.
- Pushed to `dev` and confirmed all 3 jobs passed in the GitHub **Actions** tab.
- Confirmed `consulat-backend:latest` and `consulat-frontend:latest` appeared under GitHub **Packages**.

---

## Phase 6: Kubernetes Deployment (Local Cluster)

### Manifests Added (`k8s/`)

| File | Location | Kubernetes Resources |
| :--- | :--- | :--- |
| **Database** | [`k8s/postgres.yaml`](./k8s/postgres.yaml) | `PersistentVolumeClaim` (`1Gi`), `Deployment` (1 replica), `ClusterIP` Service (`db:5432`). |
| **Backend** | [`k8s/backend.yaml`](./k8s/backend.yaml) | `Deployment` (2 replicas pulling `ghcr.io/0zeus01/consulat-backend:latest`), `LoadBalancer` Service (`8080`). |
| **Frontend** | [`k8s/frontend.yaml`](./k8s/frontend.yaml) | `Deployment` (2 replicas pulling `ghcr.io/0zeus01/consulat-frontend:latest`), `LoadBalancer` Service (`4200 → 80`). |

### Deployment & Resilience Verification
```bash
# Deploy all manifests
kubectl apply -f k8s/

# Smoke test: verify all 5 pods are Running
kubectl get pods
kubectl get services

# Self-healing test: delete a backend pod and confirm automatic recreation
kubectl delete pod <consulat-backend-pod-name>
kubectl get pods

# Horizontal scaling test
kubectl scale deployment consulat-backend --replicas=3
```
- Verified end-to-end functionality at `http://localhost:4200`.
- Merged `dev` into `main` via GitHub Pull Request.