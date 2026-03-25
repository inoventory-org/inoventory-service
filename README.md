# Inoventory-Service

---

Api for **Inoventory** (not a prepper app!) using Spring Boot 3 + Kotlin.

## Java Version
This project requires Java 22. Use jenv or sdkman to configure your local environment.

## Running locally
1. Create `.env` (or `.env.local`) in the service root:
   ```properties
   DB_URL=jdbc:postgresql://db.tncuiwvsvyixivsfhpqv.supabase.co:5432/postgres?sslmode=require
   DB_USERNAME=postgres
   DB_PASSWORD=your_password
   SUPABASE_PROJECT_BASE_URL=https://tncuiwvsvyixivsfhpqv.supabase.co
   SUPABASE_JWT_AUDIENCE=authenticated
   ```
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
Supabase is used for Authentication and Authorization. Every call must contain a valid Bearer token issued by Supabase.
The service does not persist its own users table; use the Supabase `auth.users` identity and store `user_id` (UUID) on domain rows.

If DB connections suddenly fail with connection refused or metadata errors, check whether Supabase has temporarily banned your IP for suspicious activity.
https://supabase.com/dashboard/project/tncuiwvsvyixivsfhpqv/database/settings

## Links
Service is running at: http://10.100.255.76:8080/  (http://inoventory.railabouni.fra.ics.inovex.io/)
