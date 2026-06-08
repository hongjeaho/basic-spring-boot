    ---

## 1. MyBatis란?

MyBatis는 **Java 애플리케이션과 데이터베이스 사이에서 SQL 매핑을 담당하는 퍼시스턴스 프레임워크**입니다. JPA처럼 SQL을 자동 생성하지 않고, **개발자가 직접 SQL을 작성**하는 대신 반복적인 JDBC 처리를 프레임워크가 대신 처리해줍니다.

### MyBatis가 대신 처리해주는 것

| 항목 | 설명 |
| --- | --- |
| 파라미터 바인딩 | Java 객체의 필드 값을 SQL의 `?` 자리에 자동으로 매핑 |
| ResultSet 매핑 | DB 조회 결과를 Java 객체로 자동 변환 |
| Statement 실행 | PreparedStatement 생성 및 실행 처리 |
| Connection 반환 | 커넥션 풀로 Connection을 안전하게 반환 |
| Spring 트랜잭션 연동 | `@Transactional`과 자동으로 연동되어 트랜잭션 관리 |

### 기본 동작 구조

개발자는 아래 두 가지만 작성하면 됩니다. **Mapper 인터페이스**와 **XML의 SQL**을 MyBatis가 연결하여 실행합니다.

```java
// 1. Mapper 인터페이스 (Java)
@Mapper
public interface UserMapper {
    User findById(Long id);
}
```

```xml
<!-- 2. SQL 정의 (XML) -->
<select id="findById" resultType="User">
    SELECT id, user_name, email
    FROM users
    WHERE id = #{id}
</select>
```

---

## 2. 왜 MyBatis를 사용하는가?

MyBatis와 JPA는 경쟁 관계가 아니라 **용도가 다른 도구**입니다. 프로젝트 성격과 SQL의 복잡도에 따라 선택하거나, 한 프로젝트 안에서 함께 사용하기도 합니다.

### 비교

| 구분 | JPA | MyBatis |
| --- | --- | --- |
| SQL 작성 | 자동 생성 | 개발자 직접 작성 |
| 복잡한 쿼리 | 제한적 | 자유롭게 작성 가능 |
| 학습 곡선 | 높음 (영속성 컨텍스트, 지연 로딩 등) | 낮음 |
| 성능 튜닝 | 어려움 | SQL 직접 제어 가능 |
| 도메인 모델 중심 | 적합 | 비적합 |

### JPA가 적합한 경우

- 단순 CRUD 중심의 서비스
- 도메인 객체 간 관계가 명확한 설계
- 빠른 개발 속도가 우선인 경우

### MyBatis가 적합한 경우

- 복잡한 다중 테이블 조회
- 대용량 데이터 처리
- 통계·집계 쿼리가 많은 시스템
- 레거시 DB 연동 (스키마 변경 불가)
- DBA와 협업하며 SQL 튜닝이 중요한 시스템

### 복잡한 통계 쿼리 예시

JPA로 표현하기 까다로운 아래 쿼리도 MyBatis에서는 XML에 그대로 작성하면 됩니다.

```sql
SELECT
    d.dept_name,
    COUNT(*)        AS employee_count,
    SUM(salary)     AS total_salary,
    AVG(salary)     AS avg_salary
FROM employee e
JOIN department d ON e.dept_id = d.id
GROUP BY d.dept_name
HAVING COUNT(*) > 10
```

---

## 3. MyBatis 설정 구조

Spring Boot는 `mybatis-spring-boot-starter`를 사용하면 자동 설정이 가능하지만, **현재 프로젝트는 명시적으로 직접 구성**하고 있습니다.

```java
@Configuration
@MapperScan(
    basePackages = {"kr.go.kaptnet"},
    sqlSessionFactoryRef = KAPA_SQL_SESSION_FACTORY,
    annotationClass = Mapper.class
)
public class MybatisConfig {
}
```

### 명시적 구성을 선택한 이유

Auto Configuration은 단일 DataSource 환경에서는 편리하지만, 다음과 같은 상황에서는 직접 구성이 필요합니다.

- **멀티 데이터소스**: 주 DB와 보조 DB를 분리해서 각각 다른 `SqlSessionFactory`를 연결해야 하는 경우
- **설정 분리**: DataSource마다 별도의 MyBatis 설정(TypeHandler, 플러그인 등)을 적용해야 하는 경우
- **대규모 프로젝트**: 설정의 명시적 관리로 유지보수성과 가시성을 높이는 경우

---

## 4. Mapper 등록

`@MapperScan`은 지정한 패키지를 스캔하여 `@Mapper`가 붙은 인터페이스를 자동으로 Spring Bean으로 등록합니다. 각 Mapper를 일일이 `@Bean`으로 등록할 필요가 없습니다.

```java
@MapperScan(
    basePackages = {"kr.go.kaptnet"},
    sqlSessionFactoryRef = KAPA_SQL_SESSION_FACTORY,
    annotationClass = Mapper.class
)
```

### 설정 항목 설명

| 설정 | 설명 |
| --- | --- |
| `basePackages` | Mapper 인터페이스를 탐색할 패키지 경로 |
| `sqlSessionFactoryRef` | 연결할 `SqlSessionFactory` Bean 이름 |
| `annotationClass` | `@Mapper`가 붙은 인터페이스만 등록 (불필요한 Bean 등록 방지) |

> **멀티 DataSource 환경 주의사항**
>
>
> `@MapperScan`을 여러 개 사용하여 다수의 `SqlSessionFactory`를 구성하는 경우, 동일한 Mapper가 여러 설정에 의해 중복 스캔되지 않도록 해야 한다.
>
> - `basePackages`는 DB별로 명확히 분리하는 것을 권장한다.
> - 동일한 `basePackages`를 사용하면서 `annotationClass = Mapper.class`로 설정할 경우, 동일한 Mapper 인터페이스가 여러 `SqlSessionFactory`에 등록되어 Bean 충돌 또는 의도하지 않은 DB 연결이 발생할 수 있다.
> - 패키지 분리가 어려운 경우에는 커스텀 Annotation(`@Db1Mapper`, `@Db2Mapper`) 또는 `markerInterface`를 사용하여 Mapper를 구분할 수 있다.
>
> **권장 예시**
>
> ```
> kr.go.kaptnet.db1.mapper
> kr.go.kaptnet.db2.mapper
> ```
>
> 와 같이 DB별 Mapper 패키지를 분리하여 관리한다.
>

### basePackages에 와일드카드를 쓰면 안 되는가?

`@ComponentScan`과 달리 `@MapperScan`의 `basePackages`는 **와일드카드(`**`)를 지원하지 않습니다.** 패키지 경로를 문자열 그대로 인식하기 때문에 아래처럼 작성해도 의도한 대로 동작하지 않습니다.

```java
// ❌ 의도한 대로 동작하지 않음 — 와일드카드 미지원
basePackages = {"kr.go.kaptnet.**.mapper"}

// ✅ 루트 패키지를 지정하면 하위 패키지 전체를 자동 스캔
basePackages = {"kr.go.kaptnet"}
```

특정 패키지만 제한하고 싶다면 **명시적으로 나열**하는 것이 유일한 방법입니다.

```java
basePackages = {
    "kr.go.kaptnet.user.mapper",
    "kr.go.kaptnet.order.mapper",
    "kr.go.kaptnet.common.mapper"
}
```

단, 현재 프로젝트처럼 `annotationClass = Mapper.class` 를 함께 사용하면 `@Mapper`가 없는 클래스는 등록되지 않으므로, 루트 패키지를 지정해도 **실질적으로 안전**합니다. 굳이 패키지를 더 좁힐 필요가 없습니다.

### Mapper 인터페이스 작성

Mapper 인터페이스는 SQL의 진입점입니다. 메서드 이름은 XML의 `id`와 정확히 일치해야 합니다.

```java
@Mapper
public interface UserMapper {

    // 단건 조회
    User findById(Long id);

    // 목록 조회
    List<User> findAll();

    // 등록 (영향받은 행 수 반환)
    int insert(User user);

    // 수정 (영향받은 행 수 반환)
    int update(User user);

    // 삭제 (영향받은 행 수 반환)
    int deleteById(Long id);
}
```

> **주의**: Mapper 인터페이스에는 SQL 실행과 결과 반환만 정의합니다. 비즈니스 로직은 Service 계층에서 처리해야 합니다.
>

---

## 5. SqlSessionFactory

`SqlSessionFactory`는 **MyBatis의 핵심 객체**로, SQL 실행에 필요한 모든 환경을 구성합니다. 애플리케이션 기동 시 한 번 생성되고, 이후 모든 SQL 실행은 이 팩토리를 통해 이루어집니다.

```java
@Bean(KAPA_SQL_SESSION_FACTORY)
public SqlSessionFactory kapaSqlSessionFactory(
        DataSource dataSource,
        ApplicationContext applicationContext
) throws Exception {

    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();

    // 1. 어떤 DB와 연결할지
    factory.setDataSource(dataSource);

    // 2. MyBatis 전역 설정 파일 위치
    factory.setConfigLocation(
            applicationContext.getResource("classpath:mybatis-config.xml")
    );

    // 3. SQL이 담긴 Mapper XML 파일 위치 (하위 디렉터리까지 전체 스캔)
    factory.setMapperLocations(
            applicationContext.getResources("classpath:mybatis-mapper/**/*.xml")
    );

    // 모든 설정은 mybatis-config.xml 에서 관리 — Java 코드에서 중복 설정하지 않음
    return factory.getObject();
}
```

### SqlSessionFactory의 주요 역할

| 역할 | 설명 |
| --- | --- |
| Mapper XML 로딩 | 기동 시 XML을 파싱하고 SQL을 메모리에 캐싱 |
| MyBatis 설정 적용 | `mybatis-config.xml`의 설정을 반영 |
| TypeHandler 등록 | Java ↔ DB 타입 변환 규칙 적용 |
| SQL 실행 환경 구성 | Connection, Statement, ResultSet 처리 환경 초기화 |

---

## 6. Mapper XML 위치

Mapper XML은 SQL이 실제로 작성되는 파일입니다. `SqlSessionFactory`에 등록된 경로에서 로딩됩니다.

```java
factory.setMapperLocations(
    applicationContext.getResources("classpath:mybatis-mapper/**/*.xml")
);
```

- `*`는 하위 디렉터리를 모두 포함한다는 의미입니다. 따라서 `mybatis-mapper` 하위 어디에 XML을 두어도 자동으로 인식됩니다.

### 권장 디렉터리 구조

XML 파일은 **도메인(기능) 단위로 분리**하는 것이 유지보수에 유리합니다. 하나의 파일에 모든 SQL을 몰아넣으면 파일이 비대해지고 충돌이 발생하기 쉽습니다.

```
resources/
└── mybatis-mapper/
    ├── user/
    │   └── UserMapper.xml
    ├── order/
    │   └── OrderMapper.xml
    └── common/
        └── CommonMapper.xml
```

---

## 7. mybatis-config.xml

MyBatis의 **전역 동작 방식**을 정의하는 설정 파일입니다. TypeHandler, 플러그인, 전역 설정 등을 이 파일에서 관리합니다.

```xml
<configuration>

    <settings>
        <!-- DB의 snake_case 컬럼을 Java의 camelCase 필드에 자동 매핑 (실무 필수) -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>

        <!-- SQL 실행 최대 대기 시간(초). 미설정 시 무한 대기 → 커넥션 풀 고갈 위험 -->
        <setting name="defaultStatementTimeout" value="30"/>

        <!-- null 값을 DB에 넣을 때 JDBC 타입 명시. Oracle 사용 시 필수 -->
        <setting name="jdbcTypeForNull" value="NULL"/>

        <!-- 조회 결과가 null인 컬럼도 Map의 키로 포함 (false면 키 자체가 없음) -->
        <setting name="callSettersOnNulls" value="true"/>

        <!-- JOIN 결과에서 모든 컬럼이 null일 때 null 대신 빈 객체 반환 (NPE 방지) -->
        <setting name="returnInstanceForEmptyRow" value="true"/>

        <!-- 지연 로딩 비활성화 명시 (기본값이지만 의도를 명확히 표현) -->
        <setting name="lazyLoadingEnabled" value="false"/>
        <setting name="aggressiveLazyLoading" value="false"/>
    </settings>

    <typeHandlers>
        <!-- Java 8 날짜/시간 타입 처리 -->
        <typeHandler handler="org.apache.ibatis.type.LocalDateTimeTypeHandler"/>
        <typeHandler handler="org.apache.ibatis.type.LocalDateTypeHandler"/>
        <typeHandler handler="org.apache.ibatis.type.LocalTimeTypeHandler"/>
    </typeHandlers>

</configuration>
```

### 추천 설정 상세 설명

**`mapUnderscoreToCamelCase`**

DB 컬럼명과 Java 필드명의 네이밍 규칙이 다르기 때문에 사용하는 설정입니다. 일반적으로 DB는 `snake_case`, Java는 `camelCase`를 사용하고 있습니다.

`mapUnderscoreToCamelCase=true`로 설정하면 MyBatis가 조회 결과의 컬럼명을 자동으로 camelCase 형태로 변환하여 Java 객체의 필드와 매핑합니다.

| DB 컬럼명 | Java 필드명 |
| --- | --- |
| user_name | userName |
| created_at | createdAt |
| dept_id | deptId |

설정하지 않으면 컬럼명과 필드명이 일치하지 않아 자동 매핑이 수행되지 않으며, 객체의 해당 필드는 `null`로 설정됩니다.  설정을 사용하지 않는 경우에는 SQL에서 별칭(alias)을 이용해 필드명과 동일하게 맞춰주어야 합니다.

```sql
SELECT
    user_name AS userName,
    created_at AS createdAt,
    dept_id AS deptId
FROM users
```

**`defaultStatementTimeout`**

미설정 시 MyBatis는 SQL 실행 타임아웃을 지정하지 않습니다. 따라서 DB나 JDBC 드라이버의 기본 정책에 따라 장시간 대기할 수 있으며, 느린 쿼리가 커넥션을 계속 점유하면 커넥션 풀 고갈로 이어질 수 있습니다.

```xml
<!-- 특정 쿼리만 타임아웃 늘리기 -->
<select id="heavyQuery" resultType="Report" timeout="120">
    ...
</select>
```

**`jdbcTypeForNull`**

MyBatis가 `null` 값을 JDBC에 전달할 때 사용할 기본 JDBC 타입을 지정한다.

```java
public class User {
  private Long id; //MyBatis는 Long 타입이라는 것을 알고 있습니다.
}
```

이처럼 대부분의 경우 MyBatis가 타입을 추론하여 정상 처리하지만, `Map<String, Object>` 사용이나 타입 추론이 어려운 파라미터에서는 JDBC 타입 관련 오류가 발생할 수 있다.

`jdbcTypeForNull="NULL"` 설정은 이러한 환경에서의 호환성을 높이기 위한 방어적 설정이다

---

## 8. TypeHandler

TypeHandler는 **Java 타입과 DB 타입 사이의 변환을 담당**합니다. MyBatis가 SQL 파라미터를 바인딩할 때, 그리고 결과를 Java 객체로 변환할 때 자동으로 호출됩니다.

### 현재 프로젝트 등록 TypeHandler

```xml
<typeHandlers>
    <typeHandler handler="org.apache.ibatis.type.LocalDateTimeTypeHandler"/>
    <typeHandler handler="org.apache.ibatis.type.LocalDateTypeHandler"/>
    <typeHandler handler="org.apache.ibatis.type.LocalTimeTypeHandler"/>
</typeHandlers>
```

### 타입 매핑 표

| Java 타입 | DB 타입 | 비고 |
| --- | --- | --- |
| `LocalDateTime` | `TIMESTAMP` | 날짜 + 시간 |
| `LocalDate` | `DATE` | 날짜만 |
| `LocalTime` | `TIME` | 시간만 |

## 9. 파라미터 바인딩

MyBatis에는 파라미터를 SQL에 넣는 방법이 두 가지 있습니다: `#{}` 와 `${}`. **두 방식의 차이를 반드시 이해하고 올바르게 사용해야 합니다.**

---

### `#{}` — 기본, 항상 우선 사용

```xml
WHERE id = #{id}
```

**실제 실행되는 SQL**

```sql
WHERE id = ?
```

MyBatis는 `#{}` 를 `?` 로 바꾸고 PreparedStatement의 파라미터로 값을 전달합니다.

**특징**

- PreparedStatement를 사용하므로 **SQL Injection이 원천 차단**됩니다.
- DB가 실행 계획을 캐싱하여 **성능에 유리**합니다.
- 문자열 값은 자동으로 따옴표가 처리됩니다.

---

### `${}` —  불가피한 경우에만, 반드시 검증 후 사용

`${}` 는 값을 SQL에 **문자열로 직접 치환**합니다. SQL Injection에 취약하므로 원칙적으로 사용을 피해야 하지만, **테이블명이나 컬럼명은 `#{}`로 바인딩 자체가 불가능**하기 때문에 완전히 금지할 수는 없습니다.

PreparedStatement의 `?` 는 값(value) 위치에만 쓸 수 있습니다. `ORDER BY user_name` 처럼 컬럼명이 들어가는 자리에는 `?`를 쓸 수 없고, DB도 이를 허용하지 않습니다.

```xml
<!-- #{} 사용 불가 — 실행 오류 발생 -->
ORDER BY #{sortColumn}   // X

<!-- ${} 를 써야만 하는 유일한 경우 -->
ORDER BY ${sortColumn}   // O (검증 후)
```

**`${}` 를 써야 하는 경우**는 테이블명, 컬럼명, `ORDER BY` 방향(`ASC`/`DESC`) 등 SQL 구조를 결정하는 식별자뿐입니다. 이 경우에는 반드시 **서버에서 Whitelist 검증 후** 사용해야 합니다.

```java
private static final Set<String> ALLOWED_SORT_COLUMNS =
    Set.of("id", "created_at", "user_name");

public List<User> getUsers(String sortColumn) {
    if (!ALLOWED_SORT_COLUMNS.contains(sortColumn)) {
        throw new IllegalArgumentException("허용되지 않은 정렬 컬럼: " + sortColumn);
    }
    return userMapper.findAllOrderBy(sortColumn);
}
```

**왜 위험한가**

사용자 입력값이 `${}` 에 들어가면, 공격자가 임의의 SQL을 삽입할 수 있습니다.

```xml
<!-- 절대 금지: 외부 입력을 ${} 에 직접 사용 -->
ORDER BY ${userInput}
```

공격자가 `userInput` 에 아래 값을 넣으면, 실행되는 SQL이 다음과 같이 변형됩니다.

```sql
ORDER BY id; DROP TABLE users; --
```

테이블이 삭제될 수 있습니다.

### 요약

| 구분 | `#{}` | `${}` |
| --- | --- | --- |
| 처리 방식 | PreparedStatement 파라미터 | 문자열 직접 치환 |
| SQL Injection | 방지됨 | 위험 (취약점 발생 가능) |
| 사용 원칙 | **기본적으로 항상 사용** | 컬럼명·테이블명 등 불가피한 경우만, Whitelist 검증 필수 |

---

## 10. resultType vs resultMap

SQL 실행 결과를 Java 객체로 변환하는 방법은 `resultType`과 `resultMap` 두 가지입니다.

### `resultType` — 단순 매핑

`resultType`은 조회 결과 한 행(row)을 하나의 Java 객체에 1:1로 매핑합니다. 컬럼명과 필드명이 일치하거나 `mapUnderscoreToCamelCase` 설정으로 자동 변환이 가능할 때 사용합니다.

```xml
<select id="findById" resultType="User">
    SELECT id, user_name, email
    FROM users
    WHERE id = #{id}
</select>
```

여기서 컬럼명이 다르다면 SQL에서 `AS`로 별칭을 주면 됩니다. 이 정도는 DTO 설계나 별칭으로 해결할 수 있습니다.

```sql
SELECT u.user_id AS id, 
	u.user_nm AS userName, 
	u.email_addr AS email ...
```

---

### `resultMap` —  중첩 구조 조립

`resultMap`이 필요한 경우는 **JOIN 결과처럼 "여러 행이 하나의 객체 트리를 구성하는 상황"** 에서 `resultType`은 근본적으로 동작하지 않습니다.

아래 JOIN 쿼리를 보면 사용자 한 명에게 주문이 3건 있으면 결과 행이 3개입니다.

```xml
<resultMap id="userResultMap" type="User">
    <id property="id" column="user_id"/>            <!-- PK는 <id> 태그 사용 -->
    <result property="userName" column="user_name"/>
    <result property="email" column="email_address"/>

    <!-- 중첩 객체 (1:1 관계) -->
    <association property="department" javaType="Department">
        <id property="id" column="dept_id"/>
        <result property="name" column="dept_name"/>
    </association>

    <!-- 컬렉션 (1:N 관계) -->
    <collection property="orders" ofType="Order">
        <id property="id" column="order_id"/>
        <result property="amount" column="order_amount"/>
    </collection>
</resultMap>

<select id="findById" resultMap="userResultMap">
    SELECT
        u.id          AS user_id,
        u.user_name,
        u.email_address,
        d.id          AS dept_id,
        d.dept_name,
        o.id          AS order_id,
        o.amount      AS order_amount
    FROM users u
    LEFT JOIN departments d ON u.dept_id = d.id
    LEFT JOIN orders o      ON u.id = o.user_id
    WHERE u.id = #{id}
</select>
```

### `resultMap` 주요 태그

**`<id>`**

PK 컬럼을 지정합니다. `<result>`와 동작은 같지만 **반드시 `<id>`로 따로 표시해야 합니다.** MyBatis가 `<id>` 값을 기준으로 동일한 객체인지 판단하기 때문입니다. JOIN 결과에서 같은 `user_id`를 가진 여러 행을 하나의 `User` 객체로 묶는 것이 이 태그 덕분입니다. `<id>` 없이 `<result>`만 쓰면 행마다 새 객체를 만들어 컬렉션이 올바르게 조립되지 않습니다.

```xml
<id property="id" column="user_id"/>
```

---

**`<result>`**

일반 컬럼과 Java 필드를 매핑합니다. `mapUnderscoreToCamelCase` 설정이 있어도 컬럼명과 필드명이 완전히 다를 때 명시적으로 지정합니다.

```xml
<result property="userName" column="user_name"/>
<result property="email"    column="email_address"/>
```

---

**`<association>`** — 1:1 관계 (중첩 객체)

하나의 행에서 특정 컬럼들을 별도 객체로 조립합니다. 예를 들어 사용자와 소속 부서처럼 사용자 1명에 부서 1개가 대응될 때 사용합니다.

```xml
<association property="department" javaType="Department">
    <id     property="id"   column="dept_id"/>
    <result property="name" column="dept_name"/>
</association>
```

```java
public class User {
    private Long       id;
    private String     userName;
    private Department department;  // <-- association 이 채우는 필드
}

public class Department {
    private Long   id;
    private String name;
}
```

---

**`<collection>`** — 1:N 관계 (컬렉션)

여러 행에 걸쳐 흩어진 데이터를 `List`로 조립합니다. JOIN 결과에서 같은 `<id>` 값을 가진 행들을 하나의 부모 객체로 묶고, `<collection>` 아래 컬럼들을 `List` 항목으로 쌓습니다.

```xml
<collection property="orders" ofType="Order">
    <id     property="id"     column="order_id"/>
    <result property="amount" column="amount"/>
    <result property="status" column="order_status"/>
</collection>
```

`property`는 부모 클래스의 필드명, `ofType`은 리스트 안 요소의 타입입니다.

```java
public class User {
    private Long        id;
    private String      userName;
    private List<Order> orders;  // <-- collection 이 채우는 필드
}
```

---

**`<association>` + `<collection>` 함께 사용**

실무에서는 두 태그를 함께 쓰는 경우가 많습니다. 사용자 한 명이 부서(1:1)에 속하면서 동시에 여러 주문(1:N)을 가진 구조입니다.

```xml
<resultMap id="userFullMap" type="User">
    <id     property="id"       column="user_id"/>
    <result property="userName" column="user_name"/>
    <result property="email"    column="email"/>

    <!-- 1:1 -->
    <association property="department" javaType="Department">
        <id     property="id"   column="dept_id"/>
        <result property="name" column="dept_name"/>
    </association>

    <!-- 1:N -->
    <collection property="orders" ofType="Order">
        <id     property="id"     column="order_id"/>
        <result property="amount" column="amount"/>
        <result property="status" column="order_status"/>
    </collection>
</resultMap>

<select id="findUserFull" resultMap="userFullMap">
    SELECT
        u.id          AS user_id,
        u.user_name,
        u.email,
        d.id          AS dept_id,
        d.dept_name,
        o.id          AS order_id,
        o.amount,
        o.status      AS order_status
    FROM users u
    LEFT JOIN departments d ON u.dept_id  = d.id
    LEFT JOIN orders      o ON u.id       = o.user_id
    WHERE u.id = #{id}
</select>
```

```java
public class User {
    private Long        id;
    private String      userName;
    private String      email;
    private Department  department;  // association
    private List<Order> orders;      // collection
}
```

---

### 태그 요약

| 태그 | 용도 | 핵심 속성 |
| --- | --- | --- |
| `<id>` | PK 매핑 + 객체 동일성 판단 기준 | `property`, `column` |
| `<result>` | 일반 컬럼 매핑 | `property`, `column` |
| `<association>` | 1:1 중첩 객체 조립 | `property`, `javaType` |
| `<collection>` | 1:N 컬렉션 조립 | `property`, `ofType` |

### 선택 기준

| 상황 | 이유 포함 |
| --- | --- |
| 단일 테이블 단순 조회 | `resultType` — 행과 객체가 1:1 대응 |
| `AS` 별칭으로 해결 가능한 컬럼명 불일치 | `resultType` — SQL 별칭으로 충분 |
| JOIN으로 여러 테이블 조회 | `resultMap` — 여러 행을 하나의 객체로 묶어야 함 |
| 중첩 객체(1:1)나 컬렉션(1:N) 매핑 | `resultMap` — `resultType`으로는 구조 자체를 표현 불가 |

---

## 11. 동적 SQL

MyBatis의 가장 강력한 기능 중 하나입니다. 조건에 따라 SQL을 동적으로 구성할 수 있어, 복잡한 검색 조건을 깔끔하게 처리할 수 있습니다.

---

### `<if>` — 조건부 SQL 추가

특정 파라미터가 있을 때만 SQL 조각을 추가합니다.

```xml
<if test="userName != null and userName != ''">
    AND user_name = #{userName}
</if>
```

---

### `<where>` — WHERE 절 자동 관리

`<if>` 태그만 사용하면 모든 조건이 null일 때 `WHERE` 가 남거나, 첫 조건 앞의 `AND` 가 문제가 됩니다. `<where>` 태그가 이를 자동으로 해결해줍니다.

```xml
<select id="search" resultType="User">
    SELECT id, user_name, email
    FROM users
    <where>
        <if test="userName != null">
            AND user_name = #{userName}
        </if>
        <if test="email != null">
            AND email = #{email}
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
    </where>
</select>
```

- 조건이 하나도 없으면 `WHERE` 자체가 생성되지 않습니다.
- 첫 번째 조건 앞의 `AND` 는 자동으로 제거됩니다.

---

### `<choose>` — 조건 분기 (Switch-Case)

여러 조건 중 하나만 선택해야 할 때 사용합니다. Java의 `switch-case` 와 동일한 개념입니다.

```xml
<choose>
    <when test="status == 'ACTIVE'">
        AND deleted_at IS NULL
    </when>
    <when test="status == 'DELETED'">
        AND deleted_at IS NOT NULL
    </when>
    <otherwise>
        <!-- 해당하는 조건 없을 때 기본 동작 -->
        AND 1 = 1
    </otherwise>
</choose>
```

---

### `<foreach>` — IN 절 처리

List나 배열을 받아 `IN (...)` 절을 생성합니다. 직접 문자열로 조합하면 SQL Injection 위험이 있지만, `<foreach>` 는 `#{}` 를 사용하므로 안전합니다.

```xml
<select id="findByIds" resultType="User">
    SELECT id, user_name
    FROM users
    WHERE id IN
    <foreach
        collection="ids"
        item="id"
        open="("
        separator=","
        close=")">
        #{id}
    </foreach>
</select>
```

`ids = [1, 2, 3]` 이면 실행 SQL은 아래와 같습니다.

```sql
WHERE id IN (?, ?, ?)
-- 파라미터: 1, 2, 3
```

---

### `<set>` — UPDATE 절 자동 관리

UPDATE 시 변경할 컬럼만 포함하도록 동적으로 생성합니다. `<where>` 와 마찬가지로 불필요한 쉼표를 자동 제거해줍니다.

```xml
<update id="update">
    UPDATE users
    <set>
        <if test="userName != null">
            user_name = #{userName},
        </if>
        <if test="email != null">
            email = #{email},
        </if>
        <if test="status != null">
            status = #{status},
        </if>
    </set>
    WHERE id = #{id}
</update>
```

마지막 쉼표(`,`)는 `<set>` 태그가 자동으로 제거합니다.

---

### `<sql>` / `<include>` — SQL 재사용

자주 사용하는 SQL 조각을 `<sql>` 로 정의하고 `<include>` 로 재사용합니다. 중복 제거와 일관성 유지에 효과적입니다.

```xml
<!-- 공통 컬럼 정의 -->
<sql id="userColumns">
    id,
    user_name,
    email,
    created_at,
    updated_at
</sql>

<!-- 재사용 -->
<select id="findById" resultType="User">
    SELECT
    <include refid="userColumns"/>
    FROM users
    WHERE id = #{id}
</select>

<select id="findAll" resultType="User">
    SELECT
    <include refid="userColumns"/>
    FROM users
    ORDER BY created_at DESC
</select>
```

---

## 12. 성능 — N+1 문제

**N+1 문제는 MyBatis 사용 시 가장 빈번하게 발생하는 성능 문제**입니다. 코드를 보면 아무 문제가 없어 보이지만, 실제로는 수백 번의 SQL이 실행될 수 있습니다.

이름의 의미는 간단합니다. 목록을 조회하는 쿼리 **1번** + 목록의 각 항목마다 추가 쿼리 **N번** = 총 **N+1번** 실행입니다.

### 잘못된 코드 — N+1 발생

```java
// 1번 실행: 사용자 100명 조회
List<User> users = userMapper.findAll();

// 100번 실행: 사용자마다 주문 조회
users.forEach(user -> {
    List<Order> orders = orderMapper.findByUserId(user.getId());
    user.setOrders(orders);
});
```

사용자가 100명이라면 총 **101번**의 SQL이 실행됩니다. 각 쿼리가 1ms라도 101ms이지만, DB 왕복 지연(latency)까지 더하면 실제로는 몇 초가 걸리게 됩니다.

### SQL 로그로 확인

```
==> Preparing: SELECT * FROM users
<== Total: 100

==> Preparing: SELECT * FROM orders WHERE user_id = ?
==> Parameters: 1(Long)
<== Total: 3

==> Preparing: SELECT * FROM orders WHERE user_id = ?
==> Parameters: 2(Long)
<== Total: 1

... (98번 더 반복)
```

### 해결 방법 1 — JOIN + resultMap (추천)

```xml
<!-- resultMap 정의 -->
<resultMap id="userWithOrdersMap" type="User">
    <id     property="id"       column="user_id"/>
    <result property="userName" column="user_name"/>
    <result property="email"    column="email"/>

    <collection property="orders" ofType="Order">
        <id     property="id"     column="order_id"/>
        <result property="amount" column="amount"/>
        <result property="status" column="order_status"/>
    </collection>
</resultMap>

<!-- JOIN으로 한 번에 조회 -->
<select id="findAllWithOrders" resultMap="userWithOrdersMap">
    SELECT
        u.id          AS user_id,
        u.user_name,
        u.email,
        o.id          AS order_id,
        o.amount,
        o.status      AS order_status
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id
    ORDER BY u.id
</select>
```

```java
List<User> users = userMapper.findAllWithOrders();
```

```java
// 사용자 (1 쪽)
public class User {
    private Long         id;
    private String       userName;
    private String       email;
    private List<Order>  orders;   // 주문 목록 (N 쪽)
}

public class Order {
    private Long   id;
    private Long   userId;   // 어느 사용자의 주문인지 (IN절 방식에서 groupingBy에 사용)
    private int    amount;
    private String status;
}
```

#### **SQL 로그**

```xml
==>  Preparing: SELECT u.id AS user_id, u.user_name, ... FROM users u LEFT JOIN orders o ...
<==  Total: 350   ← JOIN 결과 행 수 (100명 × 평균 3.5건)
```

DB 왕복이 101번에서 **1번**으로 줄었습니다. MyBatis는 `user_id`가 같은 행들을 자동으로 하나의 `User` 객체로 묶어 `List<Order>`를 채워 넣습니다.

### 해결 방법 2 — IN절 일괄 조회

JOIN이 어려운 구조이거나, 연관 데이터가 아주 많아 JOIN 결과 행이 과도하게 커지는 경우에 사용합니다. 2번의 쿼리로 해결합니다.

```java
// 1번: 사용자 목록 조회
List<User> users = userMapper.findAll();

// 2번: 전체 ID를 모아 IN절로 한 번에 조회
List<Long> userIds = users.stream()
    .map(User::getId)
    .collect(Collectors.toList());

List<Order> allOrders = orderMapper.findByUserIds(userIds);

// Java에서 조립
Map<Long, List<Order>> orderMap = allOrders.stream()
    .collect(Collectors.groupingBy(Order::getUserId));

users.forEach(user ->
    user.setOrders(orderMap.getOrDefault(user.getId(), Collections.emptyList()))
);
```

```xml
<select id="findByUserIds" resultType="Order">
    SELECT id, user_id, amount, status
    FROM orders
    WHERE user_id IN
    <foreach collection="list" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
</select>
```

#### **SQL 로그**

```xml
==>  Preparing: SELECT * FROM users
<==  Total: 100

==>  Preparing: SELECT ... FROM orders WHERE user_id IN (?,?,?, ... ,?)
==>  Parameters: 1(Long), 2(Long), 3(Long), ... , 100(Long)
<==  Total: 350
```

101번에서 **2번**으로 줄었습니다.

### 방법 선택 기준

| 상황 | 권장 방법 |
| --- | --- |
| 일반적인 1:N 목록 조회 | JOIN + `resultMap` |
| 연관 데이터가 매우 많아 JOIN 결과 행이 폭증하는 경우 | IN절 일괄 조회 |
| 페이징과 함께 사용 | IN절 일괄 조회 (JOIN + 페이징은 결과 수가 어긋남) |

---

## 13. SQL 로그 확인

개발 환경에서 **SQL 로그 확인은 선택이 아닌 필수**입니다. 실행되는 SQL과 파라미터, 결과 건수를 직접 확인해야 N+1 문제, 잘못된 인덱스 사용, 예상치 못한 쿼리 등을 사전에 발견할 수 있습니다.

### 설정

```yaml
# application.yml (개발 환경)
logging:
  level:
    org.mybatis: DEBUG
    # 특정 Mapper만 보고 싶다면:
    kr.go.kaptnet.mapper: DEBUG
```

### 로그 출력 예시

```
==> Preparing: SELECT id, user_name, email FROM users WHERE id = ?
==> Parameters: 1(Long)
<== Total: 1
```

### 로그 항목 설명

| 항목 | 의미 |
| --- | --- |
| `==> Preparing:` | 실제 실행되는 SQL (`?` 로 치환된 상태) |
| `==> Parameters:` | `?` 에 바인딩되는 파라미터 값과 타입 |
| `<== Total:` | 조회된 결과 행 수 |

> **운영 환경에서는 로그 레벨을 `INFO` 이상으로 설정**해야 합니다. `DEBUG` 로 설정하면 모든 SQL이 로그에 기록되어 성능 저하와 민감 정보 노출의 위험이 있습니다.
>

---

## 14. 실무 권장사항

### DTO 분리

`Map<String, Object>` 사용은 지양합니다. 타입 안전성이 없고, 어떤 값이 들어오는지 코드만 봐서는 알 수 없어 유지보수가 매우 어렵습니다.

```java
// ❌ 비권장
Map<String, Object> params = new HashMap<>();
params.put("userName", "홍길동");

// ✅ 권장: 목적에 맞는 DTO 분리
UserSearchCondition condition = new UserSearchCondition();  // 검색 조건
UserCreateRequest   createRequest = new UserCreateRequest(); // 등록 요청
UserUpdateRequest   updateRequest = new UserUpdateRequest(); // 수정 요청
UserResponse        response = new UserResponse();           // 응답
```

DTO를 분리하면 어떤 필드가 필요한지 명확하고, IDE의 자동완성과 컴파일 오류 검출의 도움을 받을 수 있습니다.

---

### Mapper 역할 제한

Mapper는 **SQL 실행과 결과 매핑만** 담당해야 합니다. Mapper에 비즈니스 로직이 들어가면 테스트가 어려워지고 책임 분리가 무너집니다.

```java
// ❌ 잘못된 예: Mapper에 비즈니스 로직 포함
@Mapper
public interface UserMapper {
    default User findActiveUser(Long id) {
        User user = findById(id);
        if (user.getStatus() == Status.DELETED) {  // 비즈니스 로직이 Mapper에!
            throw new UserDeletedException();
        }
        return user;
    }
}

// ✅ 올바른 예: Mapper는 SQL만, 비즈니스 로직은 Service로
@Service
public class UserService {
    public User findActiveUser(Long id) {
        User user = userMapper.findById(id);     // Mapper: SQL만
        if (user.getStatus() == Status.DELETED) { // Service: 비즈니스 로직
            throw new UserDeletedException();
        }
        return user;
    }
}
```

---

### SQL 재사용 (`<sql>` / `<include>`)

동일한 컬럼 목록이나 조건이 여러 쿼리에 반복된다면 `<sql>` 로 추출하세요. 컬럼 추가/삭제 시 한 곳만 수정하면 됩니다.

```xml
<sql id="userColumns">
    id, user_name, email, status, created_at
</sql>

<select id="findById" resultType="User">
    SELECT <include refid="userColumns"/>
    FROM users WHERE id = #{id}
</select>

<select id="findAll" resultType="User">
    SELECT <include refid="userColumns"/>
    FROM users ORDER BY created_at DESC
</select>
```

---