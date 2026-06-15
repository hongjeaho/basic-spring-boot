---

## 1. 예외 처리란?

Spring Boot 애플리케이션에서 **예외 처리는 서비스 안정성과 클라이언트 경험을 좌우하는 핵심 설계**입니다. 예외를 처리하지 않으면 클라이언트에게 스택 트레이스나 의미 없는 500 에러가 그대로 노출됩니다.

### 예외 처리가 없을 때의 문제

| 문제 | 설명 |
| --- | --- |
| 민감 정보 노출 | 스택 트레이스에 내부 패키지 구조, DB 정보 등이 노출될 수 있음 |
| 일관성 없는 응답 | 에러마다 응답 형식이 달라 클라이언트 처리가 어려움 |
| 디버깅 어려움 | 어떤 에러인지 명확한 코드와 메시지가 없어 원인 파악이 느림 |
| 운영 모니터링 불가 | 에러 분류 기준이 없어 알람 설정이나 통계 집계가 어려움 |

### 목표

클라이언트에게는 **일관된 형식의 에러 응답**을 제공하고, 서버에서는 **에러를 추적하고 모니터링**할 수 있는 구조를 만드는 것입니다.

---

## 2. 예외 처리 흐름

Spring MVC에서 예외가 발생하면 다음 순서로 처리됩니다.

```
HTTP 요청
    ↓
DispatcherServlet
    ↓
Controller (예외 발생)
    ↓
@ControllerAdvice (전역 처리)
    ↓ (없으면)
Spring 기본 에러 처리 (/error)
```

---

## 3. @ControllerAdvice — 전역 예외 처리

`@ControllerAdvice`는 **모든 컨트롤러에서 발생하는 예외를 한 곳에서 처리**합니다. REST API에서는 `@RestControllerAdvice`를 사용합니다 (`@ControllerAdvice` + `@ResponseBody` 결합).

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 처리 (BookNotFoundException 등 모든 하위 예외 포함)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<KapaApiErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(KapaApiErrorResponse.of(e.getErrorCode(), e.getMessage()));  // 메시지는 Service에서 결정
    }

    // Validation 실패 (@Valid 검증 오류)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<KapaApiErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> details = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> Optional.ofNullable(fe.getDefaultMessage()).orElse("invalid")
                ));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(KapaApiErrorResponse.of(ErrorCode.VALIDATION_FAILED, "입력값이 올바르지 않습니다.", details));
    }

    // DB 연결 실패, 쿼리 타임아웃 등 JDBC 레벨 오류
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<KapaApiErrorResponse> handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        log.error("DataAccessException at [{}]", request.getRequestURI(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(KapaApiErrorResponse.of(ErrorCode.DATABASE_ERROR, "서버 오류가 발생했습니다."));
    }

    // MyBatis Mapper XML 문법 오류, resultType 매핑 실패 등
    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseEntity<KapaApiErrorResponse> handleMyBatisSystemException(MyBatisSystemException e, HttpServletRequest request) {
        log.error("MyBatisSystemException at [{}]", request.getRequestURI(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(KapaApiErrorResponse.of(ErrorCode.DATABASE_ERROR, "서버 오류가 발생했습니다."));
    }

    // 그 외 처리하지 못한 예외 (최후의 방어선)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<KapaApiErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]", request.getRequestURI(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(KapaApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
    }
}
```

> **주의**: `Exception.class` 핸들러는 반드시 마지막에 선언하거나 `@Order`로 우선순위를 낮춰야 합니다. 가장 넓은 범위이기 때문에 먼저 등록되면 다른 핸들러보다 먼저 동작할 수 있습니다.
>

---

## 4. 공통 Response 구조

성공 응답(`KapaApiResponse`)과 에러 응답(`KapaApiErrorResponse`)을 **분리**하여 각각의 역할에 맞는 구조를 가집니다.

### 성공 응답 — KapaApiResponse

```java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApiResponse", title = "성공 응답")
public class KapaApiResponse<T> {

    private boolean success;  // 항상 true
    private T data;
    private Object meta;      // 페이지 정보 등 부가 정보 (선택)

    // 내부 빌더용
    private static <T> KapaApiResponse<T> of(T data) {
        return KapaApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    private static <T> KapaApiResponse<T> of(T data, Object meta) {
        return KapaApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(meta)
                .build();
    }

    // 200 OK
    public static <T> ResponseEntity<KapaApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(KapaApiResponse.of(data));
    }

    // 200 OK (+ meta)
    public static <T> ResponseEntity<KapaApiResponse<T>> ok(T data, Object meta) {
        return ResponseEntity.ok(KapaApiResponse.of(data, meta));
    }

    // 201 Created
    public static <T> ResponseEntity<KapaApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(KapaApiResponse.of(data));
    }
}
```

### 에러 응답 — KapaApiErrorResponse

```java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApiErrorResponse", title = "에러 응답")
public class KapaApiErrorResponse {

    @Builder.Default
    private boolean success = false;  // 항상 false
    private ErrorBody error;
    private LocalDateTime timestamp;

    public static KapaApiErrorResponse of(ErrorCode code, String message) {
        return of(code, message, null);
    }

    public static KapaApiErrorResponse of(ErrorCode code, String message, Map<String, String> errors) {
        return KapaApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(ErrorBody.builder()
                        .code(code.name())
                        .message(message)
                        .details(Optional.ofNullable(errors).orElse(Collections.emptyMap()))
                        .build())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorBody {
        private String code;                // 에러 코드 (예: BUSINESS_ERROR)
        private String message;             // 에러 메시지
        private Map<String, String> details; // Validation 필드별 오류 (선택)
    }
}
```

> **`@Builder.Default` 사용 이유**
>

> `@Builder`와 기본값을 함께 쓸 때 `@Builder.Default`가 없으면 빌더로 생성 시 기본값이 무시됩니다. `success = false`가 항상 보장되려면 반드시 선언해야 합니다.
>

### 응답 예시

**성공 응답**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Clean Code",
    "author": "Robert C. Martin"
  }
}
```

**성공 응답 (페이징 포함)**

```json
{
  "success": true,
  "data": [...],
  "meta": { "page": 1, "size": 10, "totalCount": 53 }
}
```

**에러 응답 (단순)**

```json
{
  "success": false,
  "error": {
    "code": "BUSINESS_ERROR",
    "message": "책을 찾을 수 없습니다. (id: 42)",
    "details": {}
  },
  "timestamp": "2026-06-14T10:30:00"
}
```

**에러 응답 (Validation)**

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "입력값이 올바르지 않습니다.",
    "details": {
      "title": "제목은 필수입니다.",
      "price": "가격은 0 이상이어야 합니다."
    }
  },
  "timestamp": "2026-06-14T10:30:00"
}
```

---

## 5. Validation

Spring Boot는 `spring-boot-starter-validation` 의존성을 통해 Bean Validation을 지원합니다.

### 의존성 추가

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### 주요 애노테이션

| 애노테이션 | 대상 타입 | 설명 |
| --- | --- | --- |
| `@NotNull` | 모든 타입 | null 불허 |
| `@NotBlank` | String | null, 빈 문자열, 공백 불허 |
| `@NotEmpty` | String, Collection | null, 빈 값 불허 |
| `@Size(min, max)` | String, Collection | 길이/크기 범위 |
| `@Min(value)` | 숫자 | 최솟값 |
| `@Max(value)` | 숫자 | 최댓값 |
| `@Email` | String | 이메일 형식 |
| `@Pattern(regexp)` | String | 정규식 패턴 |
| `@Positive` | 숫자 | 양수 (0 제외) |
| `@PositiveOrZero` | 숫자 | 0 이상 |
| `@Past` | 날짜 | 과거 날짜 |
| `@Future` | 날짜 | 미래 날짜 |

### Request DTO에 적용

```java
@Getter
public class BookCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "저자는 필수입니다.")
    private String author;

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @Size(max = 20, message = "ISBN은 20자 이하여야 합니다.")
    private String isbn;  // 선택 항목 (null 허용)
}
```

### Controller에서 @Valid 적용

```java
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<KapaApiResponse<List<BookDto>>> getAllBooks() {
        return KapaApiResponse.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KapaApiResponse<BookDto>> getBookById(@PathVariable Long id) {
        return KapaApiResponse.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<KapaApiResponse<BookDto>> createBook(
            @RequestBody @Valid BookCreateRequest request) {
        return KapaApiResponse.created(bookService.createBook(request));
    }
}
```

`@Valid`가 없으면 Validation 애노테이션이 선언되어 있어도 **검증이 실행되지 않습니다.**

### @Validated vs @Valid

| 구분 | @Valid | @Validated |
| --- | --- | --- |
| 출처 | Jakarta EE 표준 | Spring 전용 |
| 그룹 검증 | 미지원 | 지원 |
| 메서드 파라미터 | 지원 | 지원 |
| 클래스 레벨 | 미지원 | 지원 (메서드 파라미터 검증) |

실무에서는 그룹 검증이 필요한 경우가 아니라면 `@Valid`를 사용하는 것이 일반적입니다.

### 그룹 검증이란?

등록(`Create`)과 수정(`Update`) 시 검증 규칙을 다르게 적용하는 기능입니다. 예를 들어 등록 시에는 `title`이 필수지만, 수정 시에는 선택으로 처리하고 싶을 때 그룹을 나눠 `@Validated(Create.class)`, `@Validated(Update.class)`처럼 상황에 맞는 검증만 실행할 수 있습니다.

### 그룹 검증을 실무에서 잘 사용하지 않는 이유

그룹 검증을 적용하려면 아래와 같이 3가지를 모두 일치시켜야 합니다.

```java
// 1. 그룹 인터페이스 정의
public interface CreateGroup {}
public interface UpdateGroup {}

// 2. DTO 필드에 그룹 지정
@NotBlank(groups = CreateGroup.class)  // 등록 시에만 필수
private String title;

// 3. Controller에서 그룹 지정
@PostMapping
public ResponseEntity<?> create(@RequestBody @Validated(CreateGroup.class) BookRequest request) {}

@PutMapping("/{id}")
public ResponseEntity<?> update(@RequestBody @Validated(UpdateGroup.class) BookRequest request) {}
```

세 곳에 그룹을 모두 맞춰줘야 하기 때문에 **한 곳이라도 누락하면** 검증이 실행되지 않는 휴먼 에러가 발생하기 쉬운 구조입니다. 또한 DTO 하나에 등록/수정 로직이 섞여 사실상 **DTO를 분리한 것과 복잡도가 비슷한데 가독성만 떨어집니다.**

따라서 실무에서는 그룹 검증 대신 **DTO를 용도별로 분리**하는 방식을 선호합니다.

```java
// ✅ 권장: 등록/수정 DTO 분리 — 에러 없고 의도가 명확
class BookCreateRequest { // 등록용: title 필수
    @NotBlank private String title;
    @NotNull  private Integer price;
}

class BookUpdateRequest { // 수정용: title 선택
    private String title;
    @NotNull private Integer price;
}
```

---

## 6. 실무 에러 코드 패턴

에러 코드는 `Enum`으로 관리합니다. 도메인별로 세분화하지 않고 **공통 분류 코드만** 정의하고, 구체적인 메시지는 예외 클래스에서 담습니다. 도메인마다 에러 코드를 추가하면 도메인이 늘수록 Enum이 비대해지고, 어차피 메시지는 예외 클래스에서 따로 관리하므로 중복만 생깁니다.

### ErrorCode Enum

```java
/**
 * 애플리케이션 전반에서 발생할 수 있는 에러 상태를 정의한 열거형 클래스.
 *
 * - VALIDATION_FAILED    : 입력 데이터 검증 실패
 * - INTERNAL_SERVER_ERROR: 서버 내부 알 수 없는 오류
 * - DATABASE_ERROR       : 데이터베이스 관련 예외
 * - BUSINESS_ERROR       : 비즈니스 로직 오류 (미존재, 중복, 상태 오류 등)
 * - AUTH_REQUIRED        : 인증되지 않은 요청
 * - FORBIDDEN            : 권한 없는 접근
 */
public enum ErrorCode {

    VALIDATION_FAILED,
    INTERNAL_SERVER_ERROR,
    DATABASE_ERROR,
    BUSINESS_ERROR,
    AUTH_REQUIRED,
    FORBIDDEN;
}
```

### 비즈니스 예외 클래스

분류 코드는 `BUSINESS_ERROR`로 통일하고, 구체적인 메시지는 각 예외 클래스에서 담습니다.

```java
// 최상위 비즈니스 예외
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
```

```java
// 도메인별 예외 — 메시지는 받아서 넣음
public class BookNotFoundException extends BusinessException {
    public BookNotFoundException(String message) {
        super(ErrorCode.BUSINESS_ERROR, message);
    }
}

public class DuplicateIsbnException extends BusinessException {
    public DuplicateIsbnException(String message) {
        super(ErrorCode.BUSINESS_ERROR, message);
    }
}
```

### Service에서 사용 — 현재 코드 적용

메시지는 **비즈니스 로직에 가장 가까운 Service에서 결정**합니다. `GlobalExceptionHandler`는 `e.getMessage()`로 그대로 받아 쓰기만 하면 됩니다.

```java
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;

    public List<BookDto> getAllBooks() {
        return bookMapper.findAll();
    }

    public BookDto getBookById(Long id) {
        BookDto book = bookMapper.findById(id);
        if (book == null) {
            throw new BookNotFoundException("책을 찾을 수 없습니다. (id: " + id + ")");
        }
        return book;
    }

    public List<BookDto> searchBooks(BookSearchCondition condition) {
        return bookMapper.search(condition);
    }
}
```

---

## 7. 전체 흐름 정리

지금까지 배운 내용이 실제로 어떻게 연결되는지 흐름으로 정리합니다.

```
[클라이언트 요청] GET /books/42
    ↓
BookController.getBookById(42)
    ↓
BookService.getBookById(42)
    ↓
bookMapper.findById(42) → null 반환
    ↓
throw new BookNotFoundException("책을 찾을 수 없습니다. (id: 42)")
    ↓
@RestControllerAdvice → GlobalExceptionHandler.handleBusinessException()
    ↓
ResponseEntity.status(HttpStatus.BAD_REQUEST)
    + KapaApiErrorResponse.of(ErrorCode.BUSINESS_ERROR, e.getMessage())
    ↓
[클라이언트 응답] HTTP 400
{
  "success": false,
  "error": {
    "code": "BUSINESS_ERROR",
    "message": "책을 찾을 수 없습니다. (id: 42)",
    "details": {}
  },
  "timestamp": "2026-06-14T10:30:00"
}
```

### 계층별 역할 요약

| 계층 | 역할 |
| --- | --- |
| DTO (`@Valid`) | 입력값 형식 검증 |
| `BookService` | 비즈니스 규칙 위반 시 메시지를 담아 예외 발생 |
| `ErrorCode` | 에러 분류 코드 정의 (BUSINESS_ERROR 등) |
| `GlobalExceptionHandler` | HTTP 상태 코드 결정 + 예외를 `KapaApiErrorResponse`로 변환 |
| `KapaApiResponse` | 성공 시 data + meta 구조 |
| `KapaApiErrorResponse` | 실패 시 에러 코드 + 메시지 + details + timestamp 구조 |

---

## 8. 실무 권장사항

### 예외는 구체적으로 던진다

`RuntimeException`이나 `Exception`을 직접 던지지 않습니다. 어떤 문제인지 명확히 표현하는 예외 클래스를 사용해야 `GlobalExceptionHandler`에서 정확히 처리할 수 있습니다.

```java
// ❌ 비권장
throw new RuntimeException("책 미존재");

// ✅ 권장
throw new BookNotFoundException("책을 찾을 수 없습니다. (id: " + id + ")");
```

### 서버 에러 로그는 반드시 남기고, 클라이언트에는 불친절한 메시지를 보낸다

`DataAccessException`이나 `Exception` 같은 서버 내부 오류는 `e.getMessage()`를 그대로 응답에 담으면 SQL, 내부 테이블명, 경로 등이 노출될 수 있습니다. 서버에는 전체 스택 트레이스를 로그로 남기고, 클라이언트에는 의도적으로 정보를 숨기는 불친절한 메시지를 보내야 합니다.

```java
// ❌ 절대 금지: 내부 오류 메시지를 그대로 응답
@ExceptionHandler(DataAccessException.class)
public ResponseEntity<KapaApiErrorResponse> handleDataAccessException(DataAccessException e) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(KapaApiErrorResponse.of(ErrorCode.DATABASE_ERROR, e.getMessage()));  // SQL 등 노출 위험!
}

// ✅ 권장: 로그는 상세하게, 클라이언트는 최소한의 메시지만
@ExceptionHandler(DataAccessException.class)
public ResponseEntity<KapaApiErrorResponse> handleDataAccessException(DataAccessException e, HttpServletRequest request) {
    log.error("DataAccessException at [{}]", request.getRequestURI(), e);  // 서버: 전체 스택 트레이스 기록
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(KapaApiErrorResponse.of(ErrorCode.DATABASE_ERROR, "서버 오류가 발생했습니다."));  // 클라이언트: 최소한의 정보만
}
```

### 예외 계층 구조를 활용한다

`BusinessException` 하나만 등록해도 `BookNotFoundException`, `DuplicateIsbnException` 등 모든 하위 예외를 처리할 수 있습니다.

```java
@ExceptionHandler(BusinessException.class)
public ResponseEntity<KapaApiErrorResponse> handleBusinessException(BusinessException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(KapaApiErrorResponse.of(e.getErrorCode(), e.getMessage()));
}
```