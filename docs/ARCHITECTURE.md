# Spring Boot Architecture Guide

범용 Spring Boot 프로젝트를 위한 아키텍처 설계 원칙 가이드

## Table of Contents

1. [Architecture Principles](#architecture-principles)
2. [Domain-Driven Design Patterns](#domain-driven-design-patterns)
3. [Security Architecture](#security-architecture)
4. [Testing Strategy](#testing-strategy)
5. [API Design](#api-design)
6. [Development Workflow](#development-workflow)

---

## Architecture Principles

프로젝트의 복잡도와 요구사항에 따라 두 가지 아키텍처 방식을 선택할 수 있다.

## Approach 1: Three-Layer Architecture

**권장 대상:** CRUD 중심 애플리케이션, 작은 팀, 빠른 프로토타이핑

### 구조

애플리케이션을 명확한 책임을 가진 세 개의 계층으로 분리한다.

```
┌─────────────────────────────────────┐
│   Presentation Layer                │  REST API, DTO 변환, HTTP 처리
│   (application 패키지)               │
├─────────────────────────────────────┤
│   Domain Layer                      │  Entity, Service, Repository Interface
│   (domain 패키지)                    │  비즈니스 로직, 트랜잭션 관리
├─────────────────────────────────────┤
│   Infrastructure Layer              │  기술 구현, Repository 구현
│   (infrastructure 패키지)            │  외부 시스템 연동
└─────────────────────────────────────┘
```

### 의존성 규칙

```
Presentation (application) ──┐
                             ├──> Domain <── Infrastructure
                             │
                             └──> Infrastructure (제한적)
```

### 핵심 원칙

- **Domain Service가 트랜잭션 관리**: `@Transactional` 직접 사용
- **Spring 친화적**: `@Service`, `@Repository` 활용
- **단순한 흐름**: Controller → Service → Repository
- **빠른 개발**: Boilerplate 최소화

### 패키지 구조

```
src/main/java/
├── application/                      # Presentation Layer
│   ├── user/
│   │   ├── UserRestController.java
│   │   ├── UserPostRequestDTO.java
│   │   └── UserModel.java
│   ├── article/
│   │   └── ArticleRestController.java
│   └── security/
│       └── SecurityConfiguration.java
│
├── domain/                           # Domain Layer
│   ├── user/
│   │   ├── User.java
│   │   ├── Email.java
│   │   ├── Password.java
│   │   ├── UserService.java         # @Service, @Transactional
│   │   ├── UserRepository.java      # Interface
│   │   └── UserSignUpRequest.java
│   └── article/
│       ├── Article.java
│       └── ArticleService.java
│
└── infrastructure/                   # Infrastructure Layer
    ├── persistence/
    │   └── SpringDataJPAConfiguration.java
    └── security/
        └── JWTConfiguration.java
```

### 장점

- **단순함**: 이해하기 쉽고 빠르게 개발 가능
- **Spring 표준**: Spring Boot 생태계와 자연스럽게 통합
- **적은 Boilerplate**: 중간 객체가 적음
- **작은 팀에 적합**: 빠른 의사결정과 개발 가능

### 단점

- **책임 경계 모호**: Domain Service가 너무 많은 역할
- **프레임워크 의존**: Domain이 Spring에 의존
- **재사용성 제한**: 다른 인터페이스 추가 시 수정 범위 큼

---

## Approach 2: Four-Layer Architecture (Hexagonal)

**권장 대상:** 복잡한 비즈니스 로직, 다중 인터페이스, 장기 프로젝트

### 구조

Presentation과 Application을 분리하여 Use Case를 명확히 표현한다.

```
┌─────────────────────────────────────┐
│   Presentation Layer                │  HTTP 프로토콜 처리만
│   (presentation 패키지)              │  Controller, Request/Response DTO
├─────────────────────────────────────┤
│   Application Layer                 │  Use Case 조정, 트랜잭션 관리
│   (application 패키지)               │  Command, Query, Port 인터페이스
├─────────────────────────────────────┤
│   Domain Layer                      │  순수 비즈니스 로직
│   (domain 패키지)                    │  Entity, Value Object, Domain Service
├─────────────────────────────────────┤
│   Infrastructure Layer              │  기술 구현, Adapter
│   (infrastructure 패키지)            │  Repository 구현, 외부 API
└─────────────────────────────────────┘
```

### 의존성 규칙 (Hexagonal Architecture)

```
Presentation ──> Application ──> Domain <── Infrastructure
                     │                           │
                     └───────────────────────────┘
                         (Port 인터페이스를 통한 연결)
```

### 핵심 원칙

- **Use Case 중심**: Application Service가 비즈니스 흐름 조정
- **Domain 순수성**: Domain은 프레임워크 독립적
- **Port & Adapter**: 인터페이스로 계층 간 결합 분리
- **명확한 책임**: 각 계층의 역할이 명확함

### 패키지 구조

```
src/main/java/
├── presentation/                     # Presentation Layer
│   ├── rest/
│   │   ├── user/
│   │   │   ├── UserRestController.java
│   │   │   ├── UserRequest.java
│   │   │   └── UserResponse.java
│   │   └── article/
│   │       └── ArticleRestController.java
│   └── security/
│       └── SecurityConfiguration.java
│
├── application/                      # Application Layer
│   ├── user/
│   │   ├── CreateUserUseCase.java   # @Service, @Transactional
│   │   ├── UpdateUserUseCase.java
│   │   ├── command/
│   │   │   └── CreateUserCommand.java
│   │   ├── result/
│   │   │   └── UserResult.java
│   │   └── port/
│   │       ├── in/
│   │       │   └── CreateUserPort.java      # Inbound Port
│   │       └── out/
│   │           └── LoadUserPort.java        # Outbound Port
│   └── article/
│       └── PublishArticleUseCase.java
│
├── domain/                           # Domain Layer
│   ├── user/
│   │   ├── User.java
│   │   ├── Email.java
│   │   ├── Password.java
│   │   ├── UserDomainService.java   # 순수 비즈니스 로직 (no @Transactional)
│   │   └── UserRepository.java      # Interface (Outbound Port)
│   └── article/
│       ├── Article.java
│       └── ArticleDomainService.java
│
└── infrastructure/                   # Infrastructure Layer
    ├── persistence/
    │   ├── UserRepositoryAdapter.java       # Repository 구현
    │   └── JpaConfiguration.java
    └── security/
        └── BCryptPasswordEncoder.java
```

### 장점

- **관심사 명확히 분리**: HTTP vs 흐름 조정 vs 비즈니스 로직
- **Use Case 가시성**: 비즈니스 요구사항이 코드에 명확히 표현
- **다중 인터페이스 지원**: REST, GraphQL, gRPC 등 쉽게 추가
- **Domain 순수성**: 프레임워크 독립적이어서 테스트 용이
- **장기 유지보수성**: 명확한 경계로 변경 영향 범위 제한

### 단점

- **복잡도 증가**: 계층이 많아 파일 수 증가
- **Boilerplate**: Command, Result, Port 등 추가 객체 필요
- **학습 곡선**: 팀 전체가 개념 이해 필요
- **작은 프로젝트에는 과도**: 단순 CRUD에는 오버엔지니어링

---

## 의존성 규칙 (공통)

모든 아키텍처 방식에서 다음 규칙을 따른다:

1. **의존성은 안쪽으로**: 외부 계층이 내부 계층을 의존
2. **Domain은 독립적**: 다른 계층을 알지 못함
3. **Interface로 분리**: 추상화를 통한 결합도 감소
4. **Infrastructure는 구현체**: Domain/Application의 인터페이스 구현

---

## 구현 예시: 4-Layer Architecture

4-Layer 방식의 구체적인 구현 예시를 User 생성 Use Case로 살펴본다.

### 1. Presentation Layer

HTTP 관심사만 처리한다.

```java
// presentation/rest/user/UserRestController.java
@RestController
@RequestMapping("/api/users")
class UserRestController {
    private final CreateUserUseCase createUserUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        // 1. HTTP Request → Command 변환
        final var command = new CreateUserCommand(
            request.email(),
            request.username(),
            request.password()
        );

        // 2. Use Case 호출
        final var result = createUserUseCase.execute(command);

        // 3. Result → HTTP Response 변환
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UserResponse.from(result));
    }
}

// presentation/rest/user/UserRequest.java
record UserRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 3, max = 20) String username,
    @NotBlank @Size(min = 8) String password
) {}

// presentation/rest/user/UserResponse.java
record UserResponse(
    String email,
    String username,
    String token
) {
    static UserResponse from(UserResult result) {
        return new UserResponse(
            result.email(),
            result.username(),
            result.token()
        );
    }
}
```

### 2. Application Layer

Use Case를 조정하고 트랜잭션을 관리한다.

```java
// application/user/CreateUserUseCase.java
@Service
@Transactional
public class CreateUserUseCase {
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTSerializer jwtSerializer;

    public UserResult execute(CreateUserCommand command) {
        // 1. Domain Service로 비즈니스 규칙 검증
        userDomainService.validateEmailUniqueness(new Email(command.email()));
        userDomainService.validateUsernameUniqueness(new UserName(command.username()));

        // 2. Entity 생성 (Factory Method)
        final var user = User.create(
            new Email(command.email()),
            new UserName(command.username()),
            Password.of(command.password(), passwordEncoder)
        );

        // 3. 영속성
        final var saved = userRepository.save(user);

        // 4. JWT 토큰 생성
        final var token = jwtSerializer.jwtFromUser(saved);

        // 5. Result 반환
        return new UserResult(
            saved.getId(),
            saved.getEmail().toString(),
            saved.getName().toString(),
            token
        );
    }
}

// application/user/command/CreateUserCommand.java
public record CreateUserCommand(
    String email,
    String username,
    String password
) {}

// application/user/result/UserResult.java
public record UserResult(
    Long id,
    String email,
    String username,
    String token
) {}
```

### 3. Domain Layer

순수 비즈니스 로직만 포함한다.

```java
// domain/user/User.java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private UserName userName;

    @Embedded
    private Password password;

    // Factory Method
    public static User create(Email email, UserName userName, Password password) {
        validateUserCreation(email, userName, password);
        return new User(email, userName, password);
    }

    private static void validateUserCreation(Email email, UserName userName, Password password) {
        if (email == null || userName == null || password == null) {
            throw new IllegalArgumentException("User creation requires all fields");
        }
    }

    private User(Email email, UserName userName, Password password) {
        this.email = email;
        this.userName = userName;
        this.password = password;
    }

    protected User() {}  // JPA requirement

    // Getters
    public Long getId() { return id; }
    public Email getEmail() { return email; }
    public UserName getName() { return userName; }

    // 비즈니스 로직
    public boolean matchesPassword(String rawPassword, PasswordEncoder encoder) {
        return password.matchesPassword(rawPassword, encoder);
    }
}

// domain/user/UserDomainService.java
@Component
public class UserDomainService {
    private final UserRepository userRepository;

    public void validateEmailUniqueness(Email email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new IllegalArgumentException("Email already exists: " + email);
        });
    }

    public void validateUsernameUniqueness(UserName userName) {
        userRepository.findByUserName(userName).ifPresent(user -> {
            throw new IllegalArgumentException("Username already exists: " + userName);
        });
    }
}

// domain/user/UserRepository.java (Outbound Port)
public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(Email email);
    Optional<User> findByUserName(UserName userName);
    User save(User user);
}
```

### 4. Infrastructure Layer

기술 구현을 담당한다.

```java
// infrastructure/persistence/UserRepositoryAdapter.java
@Repository
class UserRepositoryAdapter implements UserRepository {
    private final SpringDataUserRepository springDataRepository;

    @Override
    public Optional<User> findById(Long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springDataRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return springDataRepository.save(user);
    }
}

// infrastructure/persistence/SpringDataUserRepository.java
interface SpringDataUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(Email email);
    Optional<User> findByUserName(UserName userName);
}
```

### 흐름 비교

**3-Layer 흐름:**
```
Controller → Domain Service (트랜잭션) → Repository
```

**4-Layer 흐름:**
```
Controller → Use Case (트랜잭션) → Domain Service (검증) → Repository
                                → Domain Entity (로직)
```

---

## Domain-Driven Design Patterns

### Value Object

**특징:**

- 식별자 없음
- 불변 객체
- 값의 동등성으로 비교
- 비즈니스 규칙 캡슐화

```java
@Embeddable
public class ArticleTitle {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    public ArticleTitle(String title) {
        this.title = title;
        this.slug = generateSlug(title);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-");
    }

    protected ArticleTitle() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleTitle)) return false;
        ArticleTitle that = (ArticleTitle) o;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
```

### Repository Pattern

**원칙:**

- Aggregate Root당 하나의 Repository
- Domain 계층에 인터페이스 정의
- 비즈니스 의도를 표현하는 메서드명

```java
public interface ArticleRepository {
    Optional<Article> findById(long id);
    Article save(Article article);
    void delete(Article article);

    // 비즈니스 의도 표현
    Optional<Article> findFirstByContentsTitleSlug(String slug);
    Page<Article> findAllByAuthorProfileUserName(UserName userName, Pageable pageable);
    Page<Article> findAllByUserFavoritedContains(User user, Pageable pageable);
    Page<Article> findAllByContentsTagsContains(Tag tag, Pageable pageable);

    void deleteArticleByAuthorAndContentsTitleSlug(User author, String slug);
}
```

### Entity Relationship Guidelines

**1. @Embedded vs @OneToMany**

```java
// @Embedded: Aggregate 내부 Value Object
@Entity
public class Article {
    @Embedded
    private ArticleContents contents;  // Lifecycle이 Article과 동일
}

// @OneToMany: 별도 Entity
@Entity
public class Article {
    @OneToMany(mappedBy = "article", cascade = {PERSIST, REMOVE})
    private Set<Comment> comments;  // Comment는 독립적인 Entity
}
```

**2. @JoinTable 선호**

```java
@Entity
public class User {
    @JoinTable(
        name = "user_followings",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "followee_id")
    )
    @OneToMany(cascade = REMOVE)
    private Set<User> followingUsers = new HashSet<>();
}

@Entity
public class Article {
    @JoinTable(
        name = "article_favorites",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ManyToMany(fetch = EAGER, cascade = PERSIST)
    private Set<User> userFavorited = new HashSet<>();
}
```

**3. Cascade 전략**

```java
// 자식 생명주기 완전 관리
@OneToMany(
    mappedBy = "article",
    cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
    orphanRemoval = true
)
private Set<Comment> comments;

// 연관관계만 관리
@ManyToMany(fetch = EAGER, cascade = CascadeType.PERSIST)
private Set<Tag> tags;

// Cascade 없음: 완전히 독립적
@ManyToOne(fetch = EAGER)
private User author;
```

**4. Fetch 전략**

```java
// LAZY: 기본값 (N+1 주의)
@ManyToOne(fetch = FetchType.LAZY)
private User author;

// EAGER: 항상 함께 조회
@ManyToOne(fetch = FetchType.EAGER)
private User author;

// 권장: Repository에서 Fetch Join
@Query("SELECT a FROM Article a JOIN FETCH a.author WHERE a.id = :id")
Optional<Article> findByIdWithAuthor(@Param("id") Long id);
```

---

## Security Architecture

### JWT Authentication Flow

```
Client Request
    │
    ├── Header: "Authorization: Token <jwt>"
    │
    ▼
JWTAuthenticationFilter
    │
    ├── Extract Token
    ├── Create Authentication
    │
    ▼
AuthenticationManager
    │
    ├── JWTAuthenticationProvider
    │   ├── JWTDeserializer.jwtPayloadFromJWT()
    │   ├── Verify signature
    │   ├── Check expiration
    │   └── Extract UserJWTPayload
    │
    ▼
SecurityContext
    │
    └── @AuthenticationPrincipal UserJWTPayload
```

### Password Handling Pattern

```java
// Domain Layer - Value Object
@Embeddable
class Password {
    @Column(name = "password", nullable = false)
    private String encodedPassword;

    static Password of(String rawPassword, PasswordEncoder encoder) {
        return new Password(encoder.encode(rawPassword));
    }

    private Password(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    boolean matchesPassword(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    protected Password() {}
}

// Domain Service
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(UserSignUpRequest request) {
        Password password = Password.of(request.getRawPassword(), passwordEncoder);
        return userRepository.save(User.of(email, username, password));
    }
}

// Configuration
@Configuration
public class SecurityConfiguration {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**핵심:**

- 평문 비밀번호는 Domain 진입 전 암호화
- Password Value Object는 암호화된 값만 보관
- PasswordEncoder는 Infrastructure 컴포넌트로 주입

---

## Testing Strategy

### Test Pyramid

```
        ┌─────────────┐
        │  Integration│  (20%)
        │    Tests    │
        ├─────────────┤
        │    Unit     │  (80%)
        │   Tests     │
        └─────────────┘
```

### Unit Tests

**Domain Entity 테스트**

```java
class UserTest {
    @Test
    void favoriteArticle_addsToFavorites() {
        // given
        User user = User.of(email, username, password);
        Article article = new Article(author, contents);

        // when
        Article result = user.favoriteArticle(article);

        // then
        assertThat(result.isFavorited()).isTrue();
    }
}
```

**Domain Service 테스트**

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void signUp_success() {
        // given
        UserSignUpRequest request = new UserSignUpRequest(email, username, "raw");
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = userService.signUp(request);

        // then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }
}
```

### Integration Tests

**API 통합 테스트**

```java
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private String token;

    @Order(1)
    @Test
    void register_success() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(APPLICATION_JSON)
                .content("{\"user\":{...}}"))
            .andExpect(status().isOk());
    }

    @Order(2)
    @Test
    void login_andSaveToken() throws Exception {
        String response = mockMvc.perform(post("/users/login")
                .contentType(APPLICATION_JSON)
                .content("{\"user\":{...}}"))
            .andReturn().getResponse().getContentAsString();

        token = extractToken(response);
    }

    @Order(3)
    @Test
    void getUser_withToken_success() throws Exception {
        mockMvc.perform(get("/user")
                .header(AUTHORIZATION, "Token " + token))
            .andExpect(status().isOk());
    }
}
```

---

## Development Workflow

### Feature Development Process

1. **Domain 설계**
   
   - Entity/Value Object 정의
   - 비즈니스 로직 구현

2. **Domain Service 구현**
   
   - `@Service` 클래스 작성
   - `@Transactional` 설정
   - Repository 호출

3. **Repository 정의**
   
   - Domain에 인터페이스 작성
   - Spring Data JPA 메서드명 규칙 활용

4. **Controller 구현**
   
   - Request DTO 정의
   - Domain Service 호출
   - Response Model 변환

5. **Test 작성**
   
   - Entity 단위 테스트
   - Service 단위 테스트
   - API 통합 테스트

---

## API Design

### RESTful Endpoints

```
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}

POST   /api/users/{id}/follow
DELETE /api/users/{id}/follow

GET    /api/articles/{slug}/comments
POST   /api/articles/{slug}/comments
```

### Response Format (Wrapper Object 패턴)

```json
{
  "user": {
    "email": "test@test.com",
    "token": "jwt.token.here",
    "username": "test"
  }
}
```

### HTTP Status Codes

```
200 OK              GET, PUT 성공
201 Created         POST 성공
204 No Content      DELETE 성공
400 Bad Request     잘못된 요청
401 Unauthorized    인증 실패
403 Forbidden       권한 없음
404 Not Found       리소스 없음
422 Unprocessable   Validation 실패
```

---

## References

- Domain-Driven Design: Eric Evans
- Spring Boot: https://spring.io/projects/spring-boot
- RealWorld Spec: https://github.com/gothinkster/realworld
