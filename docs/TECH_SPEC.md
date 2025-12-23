# Technology Stack Definition

## 1. Core Framework & Language
| Category | Stack | Version | Note |
|:---:|:---:|:---:|:---|
| **Language** | **Java** | 17 (LTS) | JDK 17, Record/Switch expression 활용 |
| **Framework** | **Spring Boot** | 3.2.x | Spring 6 기반 |
| **Build Tool** | **Gradle** | 8.x | Build Performance & Reliability |

## 2. Infrastructure & Persistence
| Category | Stack | Version | Note |
|:---:|:---:|:---:|:---|
| **Database (Prod)** | **MySQL** | 8.0+ | Main RDBMS |
| **Database (Local)** | **H2** | - | In-memory / Console 지원 |
| **ORM** | **Spring Data JPA** | - | JPA Implementation (Hibernate) |

## 3. Security & API
| Category | Stack | Version | Note |
|:---:|:---:|:---:|:---|
| **Authentication** | **Spring Security** | - | JWT Token 기반 인증 |
| **API Docs** | **Swagger (OpenAPI)** | 3.0 | API 명세 및 테스트 |
| **Utils** | **Lombok** | - | DTO/Entity Boilerplate 제거 |
