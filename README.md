# Inoventory - Backend Service

The backend API for Inoventory, built with **Kotlin** and **Spring Boot**.

## ✨ Features
- **RESTful API**: Manage inventory items, tags, and product information.
- **Supabase Auth**: JWT-based authentication and authorization.
- **Push Notifications**: Automated expiration tracking and Firebase push delivery.
- **Search & Filtering**: Advanced search using JPA Specifications.
- **OpenAPI/Swagger**: Built-in documentation for API evaluation.
- **CORS Support**: Configured for Flutter Web and local development.

## 🚀 Tech Stack
- **Languages**: Kotlin 2.0 / Java 22
- **Framework**: Spring Boot 3.3.3
- **Persistence**: Hibernate / Spring Data JPA
- **Database**: PostgreSQL (Supabase)
- **Deployment**: [Render.io](https://inoventory.onrender.com)
- **CI/CD**: GitHub Actions

## 🛠 Local Setup

### 1. Requirements
- **Java 22**: Ensure you have it installed (use `sdkman` or `jenv`).
- **Supabase Project**: Active project for Auth and Database.

### 2. Environment Configuration
Create a `.env` (or `.env.local`) in the service root:
```properties
DB_URL=jdbc:postgresql://your-supabase-db:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=your_password
SUPABASE_PROJECT_BASE_URL=https://your-project.supabase.co
SUPABASE_JWT_AUDIENCE=authenticated
```

### 3. Running with Gradle
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 4. Swagger UI
Interactive documentation is available at:  
`http://localhost:8080/swagger-ui/index.html`

## 🐳 Docker
A `docker-compose.yml` is provided for running dependencies (Postgres/PGAdmin) or the application itself.
- **PGAdmin**: `http://localhost:5050/browser/` (Login: `postgres@example.com` / `postgres`)

## 🚢 Deployment
The backend is currently deployed at:  
`https://inoventory.onrender.com`
(Backup URL: `http://inoventory.railabouni.fra.ics.inovex.io/`)
