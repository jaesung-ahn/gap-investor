# 개발 진행 상황 및 향후 계획 (TASKS)

## 1. 프로젝트 초기 설정 및 도메인 정의
- [x] 프로젝트 생성 및 환경 설정 (Gradle, Spring Boot)
- [x] 도메인 엔티티 정의 (`Property`, `Location`)
- [x] 핵심 비즈니스 로직 구현
    - [x] 갭(Gap) 가격 계산 (`salePrice - jeonsePrice`)
    - [x] 전세가율(JeonseRate) 계산 (`jeonsePrice / salePrice * 100`)

## 2. API 및 서비스 계층 구현 (MVP 핵심)
- [x] 매물 검색을 위한 Port/Adapter 패턴 적용 (`RealEstateDataPort`, `SearchPropertyUseCase`)
- [x] **[필터링/정렬]**
    - [x] 정렬 옵션 구현 (`SortOption`: Gap 오름차순, 전세가율 내림차순 등)
    - [x] 서비스 계층 정렬 로직 적용
    - [x] 컨트롤러 파라미터 연동
- [x] **[상세 필터링]** (PRD 요구사항)
    - [x] 가격 범위 필터 (매매가/전세가/갭 가격 Min-Max)
    - [x] 면적 및 연식 필터
- [ ] **[매물 상세 조회]**
    - [ ] 단건 매물 상세 정보 API 구현

## 3. 데이터 연동 (Data Integration)
- [ ] **실데이터 확보 전략**
    - [x] 공공데이터포털(국토부 실거래가) API 연동 또는 CSV 파싱 파이프라인 구축
    - [x] H2/MySQL DB 스키마 설계 및 JPA 엔티티 매핑 (`docs/TECH_SPEC.md`)
    - [x] `RealEstateDataPort`의 DB/API 구현체 작성

## 4. 사용자 기능 (User Features)
- [x] **북마크(관심 매물) 기능**
    - [x] 북마크 생성/삭제 API
    - [x] 내 북마크 리스트 조회 API
- [x] **사용자 인증/인가 (Authentication & Authorization)** (Required for Bookmark)
    - [x] Spring Security 설정 (CSRF disable, CORS, Session Stateless)
    - [x] JWT Token Provider 구현
    - [x] 회원가입 API (Sign-up)
    - [x] 로그인 API (Sign-in)

## 5. 프론트엔드/클라이언트 (추후 예정)
- [ ] API 연동을 위한 화면 설계 및 구현

## 6. 기술 부채 및 리팩토링 (Technical Debt & Refactoring)
- [x] **Lombok 도입 및 보일러플레이트 제거**
    - [x] DTO (`PropertySearchCondition`): `@Data` 지양, `@Builder` 및 불변 객체 패턴 적용
    - [x] Entity (`Property`): `@Getter`, `@AllArgsConstructor` 적용
    - [x] Service/Controller: `@RequiredArgsConstructor` 생성자 주입 적용
