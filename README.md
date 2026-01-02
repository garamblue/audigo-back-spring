# Audigo Backend (Spring Boot)

Spring Boot 기반의 Audigo 백엔드 API 서버입니다.

## 🛠 기술 스택

- **Framework**: Spring Boot 3.3.12
- **Java**: 17
- **Build Tool**: Gradle
- **Database**: PostgreSQL (Primary), MySQL (Secondary)
- **ORM**: JPA/Hibernate + MyBatis
- **Security**: Spring Security + JWT
- **Documentation**: Swagger/OpenAPI 3

## 🚀 실행 방법

### 개발 환경 설정

1. Java 17 설치
2. PostgreSQL 설치 및 데이터베이스 생성
3. application.properties 설정

### 빌드 및 실행

```bash
# 빌드
./gradlew clean build

# 실행
java -jar build/libs/audigo-back-0.0.1-SNAPSHOT.jar
```

### API 문서

서버 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## 📁 프로젝트 구조

```
src/main/java/com/audigo/audigo_back/
├── config/          # 설정 클래스
├── controller/      # REST API 컨트롤러
├── dto/            # 데이터 전송 객체
├── entity/         # JPA 엔티티
├── repository/     # 데이터 접근 계층
├── service/        # 비즈니스 로직
├── jwt/           # JWT 관련
└── mapper/        # MyBatis 매퍼
```

## 🔧 주요 기능

- 사용자 인증/인가 (JWT)
- 게시판 CRUD
- 파일 업로드/다운로드
- 멀티 데이터베이스 지원
- API 문서 자동 생성
- 연결된 repository: 