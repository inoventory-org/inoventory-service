# Inoventory-Service

---

Api for **Inoventory** (not a prepper app!) using Spring Boot 3 + Kotlin.

## Java Version
This project requires Java 22. Use jenv or sdkman to configure your local environment.

## Running locally
1. Start PostgresDB   
`docker compose -f docker-compose.local.yml up -d`
2. Run with Gradle  
`./gradlew bootRun --args='--spring.profiles.active=local'`

### Swagger
Swagger is running under `http://localhost:8080/swagger-ui/index.html`

### Postgres
Access PGAdmin via `http://localhost:5050/browser/`. 

Connect to the inoventory database with the following configuration:
- Hostname: postgres_app
- Username: postgres
- Password: postgres


## Auth
Keycloak is used for Authentication and Authorization. You need a user with role *inoventory-user*.
Every call must contain a valid Bearer token issued by Keycloak.

## Links
Service is running at: http://10.100.255.76:8080/  
Keycloak is running at http://10.100.255.76:8081/

