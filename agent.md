# Agent Context - Inoventory Service (Backend)

This file provide specific context for AI agents working on the Inoventory Kotlin/Spring Boot application.

## 🏛 Architecture & Domain Structure
- **Package-per-Domain**: We follow a feature-based package structure (e.g., `com.railabouni.inoventory.product`).
- **Layers within a Domain**:
  - `Controller`: REST endpoints.
  - `Service`: Business logic.
  - `Repository`: Database interaction (Spring Data JPA).
  - `entity/`: JPA entities (Persistence).
  - `dto/`: Data Transfer Objects (API contracts).
- **Interface-Driven**: Especially for external dependencies (e.g., notification providers, API connectors). This allows for easy swapping and testing.

## 🔑 Authentication & Authorization
- **Supabase JWT**: We use Supabase for Auth. Every request must carry a `Bearer` token.
- **Verification**: The backend verifies the JWT using the `authenticated` audience and Supabase secret.
- **User Identity**: The service does not have its own users table; it uses the `sub` (UUID) from the JWT to identify the owner of domain rows (e.g., `items`).

## 📂 Key Domains
- `product`: Integration with Open Food Facts and internal product cache.
- `inventory`: Core management of inventory lists and items.
- `notification`: Logic for tracking expirations and sending Firebase Push Notifications (FCM).

## 🚀 Development Patterns
- **Kotlin Features**: Use standard Kotlin idioms (coroutines, serialization, data classes).
- **Entities vs DTOs**: Never expose JPA entities directly in your controllers; always map to DTOs.
- **Search**: Use `JPA Specifications` for complex filtering and search criteria.

## 🧪 Testing
- **Unit Testing**: Use JUnit 5 and `MockK` for mocking.
- **Integration Testing**: Use `@SpringBootTest` and H2 for in-memory DB testing where appropriate.

## 🚦 Constraints
- **Java 22**: Ensure the toolchain is set to Java 22.
- **Supabase IP**: If DB connection errors occur, verify if your IP has been temporarily banned by Supabase due to suspicious volume.
- **Profiles**: Use `local` profile for local development.
