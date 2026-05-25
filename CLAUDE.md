# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.14 web application using Java 17 with domain-driven package structure. The application supports both traditional View Resolver (Thymeleaf) and RESTful API endpoints.

**Tech Stack:**
- Spring Boot 3.5.14, Java 17
- MyBatis for database access
- MySQL with HikariCP connection pooling
- Flyway for database migrations
- Spring Security with JWT authentication
- Thymeleaf for server-side rendering
- Swagger/OpenAPI for API documentation
- Lombok for reducing boilerplate

## Build & Run Commands

```bash
# Build the project
./gradlew build

# Run the application (default port: 8888)
./gradlew bootRun

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# Flyway database migration
./gradlew flywayMigrate
```

## Architecture & Package Structure

**Domain-Based Package Organization:**
```
kr.go.kaptnet/
├── config/           # Configuration classes (Security, MyBatis, Database)
├── common/           # Shared utilities, error handling, API responses
│   ├── error/        # Error response structure
│   └── success/      # Success response structure
├── auth/             # Authentication domain
│   ├── dto/          # Auth DTOs (AuthUser, BasicAuthority)
│   └── service/      # UserDetailsService implementation
└── user/             # User domain
    └── mapper/       # MyBatis mappers
```

**Key Patterns:**
- Each domain has its own package with `dto/`, `mapper/`, `service/` subpackages
- Controllers follow dual pattern: `XxxController` (Thymeleaf) and `RestXxxController` (REST API)
- MyBatis mappers use XML configuration in `src/main/resources/mybatis-mapper/`
- All API responses use `KapaApiResponse<T>` envelope for success, `KapaApiErrorResponse` for errors

## Controller Pattern

**View Resolver (Thymeleaf):**
```java
@Controller
@RequestMapping("/public/xxx")
public class XxxController {
    // Returns view names for server-side rendering
    @GetMapping
    public String list(Model model) {
        // model.addAttribute(...)
        return "xxx/list"; // resolves to templates/xxx/list.html
    }
}
```

**REST API:**
```java
@RestController
@RequestMapping("/api/xxx")
public class RestXxxController {
    // Returns JSON responses with KapaApiResponse<T> envelope
    @GetMapping
    public KapaApiResponse<List<XxxDto>> list() {
        return KapaApiResponse.of(xxxService.findAll());
    }
}
```

## Database Layer

**MyBatis Configuration:**
- Mappers are interfaces annotated with `@Mapper`
- XML mappings in `src/main/resources/mybatis-mapper/[domain]/`
- Database queries in separate XML files (e.g., `UserMapper.xml`)
- Mapper scan: `@MapperScan(basePackages = {"kr.go.kaptnet"})`

**Custom Transaction Management:**
```java
@KapaTransactional  // Custom annotation for kapaTransactionManager
public void someTransactionalMethod() { }
```

**Flyway Migrations:**
- Location: `src/main/resources/db/migration/`
- Naming: `V1__description.sql`, `V2__description.sql`
- Run with: `./gradlew flywayMigrate`

## Security Configuration

**Current State:** All requests permitted (development mode)
```java
// In SecurityConfig.java - currently disabled for development
.authorizeHttpRequests(authorizeRequests ->
    authorizeRequests.anyRequest().permitAll()
)
```

**JWT Authentication Flow:**
1. `JWTCheckFilter` validates JWT tokens at `BasicAuthenticationFilter`
2. `CustomHeaderFilter` processes custom headers before `UsernamePasswordAuthenticationFilter`
3. `UserDetailsServiceImpl` loads user details from database
4. `JwtTokenUtil` handles token generation and validation

## Error Handling

**API Error Response Structure:**
```java
KapaApiErrorResponse {
    boolean success = false;
    ErrorBody {
        String code;      // ErrorCode enum name
        String message;   // User-friendly message
        Map<String, String> details;  // Field-level errors
    }
    LocalDateTime timestamp;
}
```

**ErrorCode Enum:** Define domain-specific error codes in `common/error/ErrorCode.java`

## Configuration Files

**Database:** `kapa.domain.datasource.*` in application.yaml
- HikariCP connection pool configured via `@ConfigurationProperties`

**JWT:** `jwt.secret` and `jwt.expiration.period` in application.yaml

**Server:** Default port 8888, graceful shutdown enabled

## Code Conventions

**DO:**
- Use domain-based package structure (each domain in its own package)
- Create dual controllers for new domains (`XxxController` + `RestXxxController`)
- Use `KapaApiResponse<T>` for all REST API success responses
- Use `KapaApiErrorResponse` for all REST API error responses
- Annotate mappers with `@Mapper` and place XML files in `mybatis-mapper/[domain]/`
- Use `@KapaTransactional` for transaction management
- Add Flyway migrations for all schema changes
- Use Lombok annotations to reduce boilerplate

**DON'T:**
- Mix Thymeleaf and REST endpoints in the same controller
- Hardcode credentials or sensitive data (use environment variables)
- Return raw entities from REST controllers (always use DTOs)
- Skip XML mapper files for MyBatis queries
- Create controllers without corresponding service layer
- Use Spring's default `@Transactional` (use `@KapaTransactional` instead)
- Forget to add Flyway migration for new tables

## Testing

**Test Structure:** Mirror `src/main/java` in `src/test/java`
```
src/test/java/kr/go/kaptnet/
├── controller/      # Web layer tests (MockMvc)
├── service/         # Service layer tests (Mockito)
├── mapper/          # MyBatis mapper tests
└── integration/     # Integration tests
```

**Test Framework:** JUnit 5 with Spring Boot Test

## API Documentation

Swagger UI available at: `/public/swagger-ui/index.html`

Add OpenAPI annotations to DTOs:
```java
@Schema(name = "XxxDto", title = "XXX DTO")
public class XxxDto {
    @Schema(description = "Field description")
    private String field;
}
```

## Environment-Specific Configuration

Spring profiles: `spring.profiles.default: local` in application.yaml

Create additional profiles: `application-local.yaml`, `application-dev.yaml`, `application-prod.yaml`
