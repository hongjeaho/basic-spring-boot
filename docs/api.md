# MyBatis 핵심 기능 REST API 예시

이 문서는 MyBatis의 핵심 기능들을 사용하는 REST API 샘플을 정리한 것입니다.

## 목차

1. [Book API - resultType & 동적 SQL](#1-book-api---resulttype--동적-sql)
2. [Member API - resultMap & collection](#2-member-api---resultmap--collection)
3. [Loan API - resultMap & association & IN절](#3-loan-api---resultmap--association--in절)
4. [핵심 기능별 구현 내용](#4-핵심-기능별-구현-내용)
5. [API 테스트 예시](#5-api-테스트-예시)

---

## 1. Book API - resultType & 동적 SQL

### 개요
**구현 핵심 기능**: `resultType` (단순 매핑), 파라미터 바인딩 `#{}`, 동적 SQL (`<where>`, `<if>`)

| 기능 | 설명 |
|------|------|
| `resultType` | 조회 결과 한 행(row)을 Java 객체에 1:1로 매핑 |
| 파라미터 바인딩 `#{}` | PreparedStatement 파라미터 (SQL Injection 방지) |
| `<where>` | WHERE 절 자동 관리 (불不必要的 AND 제거) |
| `<if>` | 조건부 SQL 추가 (null 체크) |

### API 엔드포인트

| 메서드 | 경로 | 설명 |
|------|------|------|
| `getAllBooks()` | `GET /api/books` | 전체 도서 목록 조회 |
| `getBookById(id)` | `GET /api/books/{id}` | 도서 상세 조회 |
| `searchBooks(condition)` | `GET /api/books/search` | 도서 검색 (동적 조건) |

### BookMapper.xml

```xml
<!-- resultType 예시: 단순 1:1 매핑 -->
<select id="findAll" resultType="kr.go.kaptnet.book.dto.BookDto">
    SELECT
        id,
        title,
        author,
        category,
        created_at AS createdAt,
        updated_at AS updatedAt
    FROM books
    ORDER BY id
</select>

<!-- 파라미터 바인딩 #{ } 예시 -->
<select id="findById" resultType="kr.go.kaptnet.book.dto.BookDto">
    SELECT
        id,
        title,
        author,
        category,
        created_at AS createdAt,
        updated_at AS updatedAt
    FROM books
    WHERE id = #{id}  -- PreparedStatement 파라미터
</select>

<!-- 동적 SQL 예시: <where>, <if>, #{ } -->
<select id="search" resultType="kr.go.kaptnet.book.dto.BookDto">
    SELECT
        id,
        title,
        author,
        category,
        created_at AS createdAt,
        updated_at AS updatedAt
    FROM books
    <where>  <!-- 조건이 없으면 WHERE 자체 제거 -->
        <if test="title != null and title != ''">
            AND title LIKE CONCAT('%', #{title}, '%')  <!-- #{ } 사용 -->
        </if>
        <if test="author != null and author != ''">
            AND author LIKE CONCAT('%', #{author}, '%')
        </if>
        <if test="category != null and category != ''">
            AND category = #{category}
        </if>
    </where>
    ORDER BY id
</select>
```

### 실행되는 SQL 예시

**요청**: `GET /api/books/search?title=Java&category=PROGRAMMING`

```sql
-- 실제 실행되는 SQL
SELECT id, title, author, category, created_at AS createdAt, updated_at AS updatedAt
FROM books
WHERE title LIKE ?
  AND category = ?
ORDER BY id

-- 파라미터: [%Java%], [PROGRAMMING]
```

---

## 2. Member API - resultMap & collection

### 개요
**구현 핵심 기능**: `resultMap` (`<id>`, `<collection>`), N+1 문제 해결 (JOIN)

| 기능 | 설명 |
|------|------|
| `<resultMap>` | 복잡한 매핑 정의 |
| `<id>` | PK 컬럼 지정 (JOIN 결과 그룹화의 핵심) |
| `<collection>` | 1:N 관계 매핑 (여러 행 → 하나의 List) |
| N+1 해결 | JOIN으로 한 번에 조회 |

### API 엔드포인트

| 메서드 | 경로 | 설명 |
|------|------|------|
| `getMemberWithLoans(id)` | `GET /api/members/{id}` | 회원 + 대출기록 조회 |

### MemberMapper.xml

```xml
<!-- resultMap + collection 예시: N+1 문제 해결 -->
<resultMap id="memberWithLoansMap" type="kr.go.kaptnet.member.dto.MemberWithLoansDto">
    <!-- <id> 태그: PK를 지정하여 동일한 객체인지 판단 -->
    <id property="id" column="member_id"/>
    <result property="name" column="member_name"/>
    <result property="email" column="member_email"/>
    <result property="createdAt" column="member_created_at"/>
    <result property="updatedAt" column="member_updated_at"/>

    <!-- <collection> 태그: 1:N 관계 (회원 한 명의 여러 대출기록) -->
    <collection property="loans" ofType="kr.go.kaptnet.loan.dto.LoanDto">
        <id property="id" column="loan_id"/>
        <result property="memberId" column="loan_member_id"/>
        <result property="bookId" column="loan_book_id"/>
        <result property="loanDate" column="loan_date"/>
        <result property="returnDate" column="return_date"/>
    </collection>
</resultMap>

<!-- JOIN으로 한 번에 조회 - N+1 문제 해결 -->
<select id="findWithLoansById" resultMap="memberWithLoansMap">
    SELECT
        m.id          AS member_id,
        m.name        AS member_name,
        m.email       AS member_email,
        m.created_at  AS member_created_at,
        m.updated_at  AS member_updated_at,
        l.id          AS loan_id,
        l.member_id   AS loan_member_id,
        l.book_id     AS loan_book_id,
        l.loan_date   AS loan_date,
        l.return_date AS return_date
    FROM members m
    LEFT JOIN loans l ON m.id = l.member_id
    WHERE m.id = #{id}
</select>
```

### N+1 문제 해결 원리

**문제 상황** (잘못된 구현):
```java
// 1번 실행: 회원 100명 조회
List<Member> members = memberMapper.findAll();

// 100번 실행: 회원마다 대출 조회
members.forEach(member -> {
    List<Loan> loans = loanMapper.findByUserId(member.getId());  // N번 실행
    member.setLoans(loans);
});
```

**해결 방법** (현재 구현):
- JOIN으로 한 번의 SQL 실행
- `<id>` 태그로 `member_id`가 같은 행들을 하나의 `Member` 객체로 그룹화
- `<collection>`으로 `loans` 리스트를 채움

### 실행되는 SQL

**요청**: `GET /api/members/1`

```sql
SELECT m.id AS member_id, m.name AS member_name, m.email AS member_email,
       m.created_at AS member_created_at, m.updated_at AS member_updated_at,
       l.id AS loan_id, l.member_id AS loan_member_id, l.book_id AS loan_book_id,
       l.loan_date AS loan_date, l.return_date AS return_date
FROM members m
LEFT JOIN loans l ON m.id = l.member_id
WHERE m.id = ?
```

---

## 3. Loan API - resultMap & association & IN절

### 개요
**구현 핵심 기능**: `resultMap` (`<association>`), `<foreach>` (IN절 일괄 조회)

| 기능 | 설명 |
|------|------|
| `<association>` | 1:1 관계 매핑 (하나의 행 → 중첩 객체) |
| `<foreach>` | IN절 생성 (`WHERE id IN (?, ?, ?)`) |
| 성능 최적화 | 일괄 조회로 DB 왕복 감소 |

### API 엔드포인트

| 메서드 | 경로 | 설명 |
|------|------|------|
| `getLoanDetail(id)` | `GET /api/loans/{id}` | 대출상세 조회 (회원 + 도서) |
| `getLoansByMemberIds(ids)` | `GET /api/loans/by-member-ids` | 회원들 대출기록 일괄 조회 |

### LoanMapper.xml

```xml
<!-- resultMap + association 예시: 1:1 관계 두 개 -->
<resultMap id="loanDetailMap" type="kr.go.kaptnet.loan.dto.LoanDetailDto">
    <id property="id" column="loan_id"/>
    <result property="memberId" column="member_id"/>
    <result property="bookId" column="book_id"/>
    <result property="loanDate" column="loan_date"/>
    <result property="returnDate" column="return_date"/>

    <!-- <association> 태그: 1:1 관계 (대출기록 ↔ 회원) -->
    <association property="member" javaType="kr.go.kaptnet.member.dto.MemberDto">
        <id property="id" column="member_id"/>
        <result property="name" column="member_name"/>
        <result property="email" column="member_email"/>
        <result property="createdAt" column="member_created_at"/>
        <result property="updatedAt" column="member_updated_at"/>
    </association>

    <!-- <association> 태그: 1:1 관계 (대출기록 ↔ 도서) -->
    <association property="book" javaType="kr.go.kaptnet.book.dto.BookDto">
        <id property="id" column="book_id"/>
        <result property="title" column="book_title"/>
        <result property="author" column="book_author"/>
        <result property="category" column="book_category"/>
        <result property="createdAt" column="book_created_at"/>
        <result property="updatedAt" column="book_updated_at"/>
    </association>
</resultMap>

<!-- association 예시: JOIN으로 대출+회원+도서 한 번에 조회 -->
<select id="findById" resultMap="loanDetailMap">
    SELECT
        l.id          AS loan_id,
        l.member_id   AS member_id,
        l.book_id     AS book_id,
        l.loan_date   AS loan_date,
        l.return_date AS return_date,
        m.id          AS member_id,
        m.name        AS member_name,
        m.email       AS member_email,
        m.created_at  AS member_created_at,
        m.updated_at  AS member_updated_at,
        b.id          AS book_id,
        b.title       AS book_title,
        b.author      AS book_author,
        b.category    AS book_category,
        b.created_at  AS book_created_at,
        b.updated_at  AS book_updated_at
    FROM loans l
    JOIN members m ON l.member_id = m.id
    JOIN books b ON l.book_id = b.id
    WHERE l.id = #{id}
</select>

<!-- 동적 SQL 예시: <foreach> - IN절 일괄 조회 (성능 최적화) -->
<select id="findByMemberIds" resultType="kr.go.kaptnet.loan.dto.LoanDto">
    SELECT
        id,
        member_id AS memberId,
        book_id AS bookId,
        loan_date AS loanDate,
        return_date AS returnDate
    FROM loans
    WHERE member_id IN
    <foreach collection="list" item="id" open="(" separator="," close=")">
        #{id}  -- 리스트의 각 요소를 파라미터로 바인딩
    </foreach>
    ORDER BY loan_date
</select>
```

### 실행되는 SQL 예시

**요청 1**: `GET /api/loans/1` (association)
```sql
-- 대출기록 1건 조회 시 회원, 도서 정보도 함께 조회
SELECT l.id, l.member_id, l.book_id, l.loan_date, l.return_date,
       m.id, m.name, m.email, m.created_at, m.updated_at,
       b.id, b.title, b.author, b.category, b.created_at, b.updated_at
FROM loans l
JOIN members m ON l.member_id = m.id
JOIN books b ON l.book_id = b.id
WHERE l.id = ?
```

**요청 2**: `GET /api/loans/by-member-ids?ids=1,2,3` (`<foreach>`)
```sql
-- 리스트 [1, 2, 3]을 IN절로 변환
SELECT id, member_id AS memberId, book_id AS bookId,
       loan_date AS loanDate, return_date AS returnDate
FROM loans
WHERE member_id IN (?, ?, ?)
ORDER BY loan_date

-- 파라미터: [1, 2, 3]
```

---

## 4. 핵심 기능별 구현 내용

### 4.1 파라미터 바인딩

**`#{}` (기본, 항상 사용)**:
- PreparedStatement 파라미터로 변환
- SQL Injection 원천 차단
- 자동으로 따옴표 처리

```xml
WHERE id = #{id}
-- 실제: WHERE id = ?
```

**`${}` (불가피한 경우만 사용)**:
- 문자열로 직접 치환
- SQL Injection 취약하므로 Whitelist 검증 필수
- 컬럼명, 테이블명 등 식별자에만 사용

```xml
ORDER BY ${sortColumn}
-- 검증 후 사용: ORDER BY title
```

### 4.2 resultType vs resultMap

| 구분 | 사용 시기 | 예시 |
|------|-----------|------|
| `resultType` | 단일 테이블 단순 조회 | `BookMapper.findAll()` |
| `resultMap` | JOIN, 중첩 구조, 복잡한 매핑 | `MemberMapper.findWithLoansById()` |

### 4.3 resultMap 태그

| 태그 | 용도 | 핵심 속성 |
|------|------|-----------|
| `<id>` | PK 매핑 + 객체 동일성 판단 | `property`, `column` |
| `<result>` | 일반 컬럼 매핑 | `property`, `column` |
| `<association>` | 1:1 중첩 객체 | `property`, `javaType` |
| `<collection>` | 1:N 컬렉션 | `property`, `ofType` |

### 4.4 동적 SQL 태그

| 태그 | 용도 | 특징 |
|------|------|------|
| `<where>` | WHERE 절 자동 관리 | 조건 없으면 WHERE 제거, 첫 AND 제거 |
| `<if>` | 조건부 SQL 추가 | `test=""`로 조건 지정 |
| `<choose>` | 조건 분기 (switch-case) | `<when>`, `<otherwise>` |
| `<foreach>` | IN절, 배치 처리 | `collection`, `item`, `open`, `separator`, `close` |
| `<set>` | UPDATE 절 자동 관리 | 불필요한 쉼표 제거 |

### 4.5 성능 최적화

**N+1 문제 해결 방법**:

1. **JOIN + resultMap** (권장):
   - 한 번의 SQL로 모든 데이터 조회
   - MyBatis가 `<id>` 기준으로 그룹화
   - `<collection>`으로 리스트 조립

2. **IN절 일괄 조회**:
   - 2번의 SQL 실행
   - `<foreach>`로 리스트를 IN절로 변환
   - Java에서 `groupingBy`로 조립

| 상황 | 권장 방법 |
|------|-----------|
| 일반적인 1:N 목록 조회 | JOIN + `resultMap` |
| 연관 데이터가 매우 많아 JOIN 결과 행이 폭증하는 경우 | IN절 일괄 조회 |
| 페이징과 함께 사용 | IN절 일괄 조회 (JOIN + 페이징은 결과 수가 어긋남) |

---

## 5. API 테스트 예시

### 5.1 애플리케이션 시작

```bash
# 포트 8888 사용 중인 프로세스 중지
kill -9 $(lsof -ti:8888 | head -1)

# 애플리케이션 시작
./gradlew bootRun

# 또는 포트 변경
./gradlew bootRun --args='--server.port=8889'
```

### 5.2 API 테스트

**1. Book API** (resultType, 동적 SQL):

```bash
# 전체 목록
curl http://localhost:8888/api/books

# 단건 조회
curl http://localhost:8888/api/books/1

# 동적 검색 (title, category 조건)
curl "http://localhost:8888/api/books/search?title=Java&category=PROGRAMMING"

# 동적 검색 (author만)
curl "http://localhost:8888/api/books/search?author=Robert"
```

**2. Member API** (collection, N+1 해결):

```bash
# 회원 + 대출기록 조회 (1명의 회원과 여러 대출기록)
curl http://localhost:8888/api/members/1

# 응답 예시:
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Hong Jae Ho",
    "email": "hongjaeho@example.com",
    "createdAt": "2026-06-08T00:00:00",
    "updatedAt": "2026-06-08T00:00:00",
    "loans": [
      {"id": 1, "memberId": 1, "bookId": 1, "loanDate": "2026-06-01", "returnDate": "2026-06-15"},
      {"id": 2, "memberId": 1, "bookId": 2, "loanDate": "2026-06-05", "returnDate": null}
    ]
  }
}
```

**3. Loan API** (association, IN절):

```bash
# 대출상세 조회 (회원 + 도서 정보 포함)
curl http://localhost:8888/api/loans/1

# 응답 예시:
{
  "success": true,
  "data": {
    "id": 1,
    "memberId": 1,
    "bookId": 1,
    "loanDate": "2026-06-01",
    "returnDate": "2026-06-15",
    "member": {
      "id": 1,
      "name": "Hong Jae Ho",
      "email": "hongjaeho@example.com"
    },
    "book": {
      "id": 1,
      "title": "Clean Code",
      "author": "Robert C. Martin",
      "category": "PROGRAMMING"
    }
  }
}

# 회원들 대출기록 일괄 조회 (<foreach> IN절)
curl "http://localhost:8888/api/loans/by-member-ids?ids=1,2,3"
```

### 5.3 Swagger UI

```
http://localhost:8888/public/swagger-ui/index.html
```

---

## 참고

- MyBatis 설정: `src/main/resources/mybatis-config.xml`
- Mapper XML 위치: `src/main/resources/mybatis-mapper/`
- Mapper 스캔: `@MapperScan(basePackages = {"kr.go.kaptnet"})`
- DB 마이그레이션: `V3__create_book_loans_tables.sql`
