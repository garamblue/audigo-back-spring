# 회원 관리 (프로필, 닉네임, 상태) 마이그레이션 완료

Node.js → Spring Boot 마이그레이션 완료 보고서

## 구현된 기능

### 1. 회원 프로필 조회
**엔드포인트:** `GET /api/member/profile`

**기능:**
- 회원 기본 정보 조회 (닉네임, 이메일, 생년월일, 성별 등)
- SNS 연동 정보 조회
- 타임존/지역 정보 조회
- 보상 잔액 조회
- 지갑 정보 조회 (성인 회원만)
- 스킨 정보 조회

**응답 구조:**
```json
{
  "info": {
    "sns": "KAKAO,NAVER",
    "stts": "1",
    "nickname": "사용자닉네임",
    "birth_dt": "1990-01-01",
    "gender": "M",
    "invit_cd": "ABC123DEF456",
    "state": "US",
    "mobile_num": "01012345678",
    "ext_key": "external_key",
    "region_cd": "KR",
    "mobile_tz": "Asia/Seoul",
    "wallet_addr": "0x...",
    "cdt": "2024-01-01T00:00:00"
  },
  "skin2D": "",
  "skin3D": "",
  "face": "",
  "rwds": 1000.50,
  "token_amt": "500.25",
  "bnb_amt": "0.05"
}
```

### 2. 닉네임 관리

#### 2-1. 닉네임 중복 확인
**엔드포인트:** `POST /api/member/nickname/check`

**요청:**
```json
{
  "data": "encrypted_data"
}
```

**복호화된 요청:**
```json
{
  "nickname": "새닉네임"
}
```

**응답:**
- `201 Created`: 사용 가능한 닉네임
- `400 Bad Request`: 이미 사용 중인 닉네임

#### 2-2. 닉네임 변경
**엔드포인트:** `PUT /api/member/nickname/update`

**요청:**
```json
{
  "data": "encrypted_data"
}
```

**복호화된 요청:**
```json
{
  "nickname": "새닉네임"
}
```

**기능:**
- 닉네임 중복 확인
- 닉네임 업데이트
- 자동 수정일시(udt) 업데이트

### 3. 회원 상태 관리

#### 3-1. 지역 상태 변경
**엔드포인트:** `PUT /api/member/state/update`

**요청:**
```json
{
  "data": "encrypted_data"
}
```

**복호화된 요청:**
```json
{
  "state": "US"
}
```

**회원 상태 코드 (stts):**
- `"1"`: 성인 활성 회원
- `"2"`: 성인 비활성 회원
- `"3"`: 탈퇴 회원
- `"4"`: 미성년 활성 회원

### 4. 휴대폰 번호 변경

**서비스 메서드:** `MemberProfileService.updateMobileNum()`

**기능:**
- 휴대폰 번호 업데이트
- 탈퇴 회원 확인

## 구현된 클래스

### 서비스
1. **MemberAuthService** - `src/main/java/com/audigo/audigo_back/service/auth/MemberAuthService.java`
   - `getMemberProfile()`: 회원 프로필 조회 메서드 추가

2. **MemberProfileService** (신규) - `src/main/java/com/audigo/audigo_back/service/member/MemberProfileService.java`
   - `checkNicknameDuplicate()`: 닉네임 중복 확인
   - `updateNickname()`: 닉네임 변경
   - `updateState()`: 지역 상태 변경
   - `updateMobileNum()`: 휴대폰 번호 변경

### 컨트롤러
1. **MemberProfileController** (신규) - `src/main/java/com/audigo/audigo_back/controller/member/MemberProfileController.java`
   - `GET /api/member/profile`: 프로필 조회
   - `POST /api/member/nickname/check`: 닉네임 중복 확인
   - `PUT /api/member/nickname/update`: 닉네임 변경
   - `PUT /api/member/state/update`: 지역 상태 변경

### 엔티티 수정
1. **MembersEntity** - `src/main/java/com/audigo/audigo_back/entity/member/MembersEntity.java`
   - `udt` 필드 추가 (수정일시)
   - `@PreUpdate` 훅 추가 (자동 수정일시 업데이트)

### 레포지토리 수정
1. **MembersRepository** - `src/main/java/com/audigo/audigo_back/repository/member/MembersRepository.java`
   - `existsByNickname()`: 닉네임 존재 여부 확인 메서드 추가

2. **SkinExchangeRepository** - `src/main/java/com/audigo/audigo_back/repository/skin/SkinExchangeRepository.java`
   - `findByMIdxOrderByCdtDesc()`: 회원 스킨 목록 조회 메서드 추가

## 보안

### 암호화/복호화
- 모든 요청/응답 데이터는 AES-256-CBC 암호화
- `AesUtil.encryptMember()`: 응답 암호화
- `AesUtil.decryptMember()`: 요청 복호화

### 인증
- Spring Security + JWT 기반 인증
- `@AuthenticationPrincipal UserDetails` 사용
- 회원 ID(mIdx)는 JWT 토큰에서 추출

### 탈퇴 회원 확인
모든 메서드에서 탈퇴 회원 확인:
```java
if ("3".equals(member.getStts()) || member.getLvDt() != null) {
    throw new IllegalStateException("탈퇴한 회원입니다.");
}
```

## 트랜잭션 관리

- **읽기 전용 트랜잭션**: `@Transactional(readOnly = true)`
  - `getMemberProfile()`
  - `checkNicknameDuplicate()`

- **쓰기 트랜잭션**: `@Transactional`
  - `updateNickname()`
  - `updateState()`
  - `updateMobileNum()`

## 자동 업데이트

### @PrePersist / @PreUpdate
`MembersEntity`에 JPA 생명주기 콜백 추가:
```java
@PrePersist
protected void onCreate() {
    cdt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    udt = LocalDateTime.now();
}
```

**장점:**
- `save()` 호출 시 자동으로 `udt` 업데이트
- 수동으로 `setUdt()` 호출 불필요
- 코드 간결성 향상

## Node.js vs Spring Boot 비교

| 기능 | Node.js | Spring Boot |
|------|---------|-------------|
| 프로필 조회 | `info.ts` | `MemberAuthService.getMemberProfile()` |
| 닉네임 확인 | `nickname.ts::members_checkNickname` | `MemberProfileService.checkNicknameDuplicate()` |
| 닉네임 변경 | `nickname.ts::members_updateNickname` | `MemberProfileService.updateNickname()` |
| 상태 변경 | `state.ts::members_stateUpdate` | `MemberProfileService.updateState()` |
| 암호화 | `decryptAES256()` / `encryptAES256()` | `AesUtil.decryptMember()` / `encryptMember()` |
| 트랜잭션 | `runCommonTransaction()` | `@Transactional` |
| 인증 | `authToken` 미들웨어 | `@AuthenticationPrincipal` |

## 테스트 필요 사항

1. **프로필 조회 API**
   - 성인 회원 프로필 조회 (지갑 정보 포함)
   - 미성년 회원 프로필 조회 (지갑 정보 제외)
   - 탈퇴 회원 접근 차단

2. **닉네임 관리**
   - 닉네임 중복 확인 (사용 가능/중복)
   - 닉네임 변경 성공
   - 중복 닉네임 변경 실패

3. **상태 관리**
   - 지역 상태 변경 성공
   - 탈퇴 회원 상태 변경 차단

## 향후 구현 필요 사항

### 1. 지역 정보 업데이트 (region.ts 마이그레이션)
- `GET /api/member/region/auth-code`: 지역 인증코드 발급
- `PUT /api/member/region/update-region`: 지역 정보 업데이트
- 1년 내 지역 변경 제한 로직

### 2. 성인 전환 (conversion.ts 마이그레이션)
- `PUT /api/member/conversion/adult`: 미성년 → 성인 전환
- Web3 약관 동의 처리
- 전자지갑 생성

### 3. SNS 계정 연결 (connect.ts 마이그레이션)
- `POST /api/member/sns/connect`: 추가 SNS 계정 연결

### 4. 관리자 회원 관리 (membersManage/members.ts)
- `GET /api/admin/members`: 회원 목록 조회
- `GET /api/admin/members/{mIdx}`: 회원 상세 조회
- 검색, 필터링, 페이징 기능

### 5. 스킨 정보 상세 조회
현재 스킨 정보는 skeleton 코드만 있음:
```java
// TODO: 스킨 상세 정보 조회 로직 추가 필요
```

## 빌드 상태

✅ **컴파일 성공**
```bash
./gradlew compileJava
BUILD SUCCESSFUL in 13s
```

**경고사항:**
- Type safety warnings (Map 타입 캐스팅) - 실행에는 영향 없음

## 마이그레이션 완료 항목

- [x] 회원 프로필 조회 API
- [x] 닉네임 중복 확인 API
- [x] 닉네임 변경 API
- [x] 지역 상태 변경 API
- [x] 휴대폰 번호 변경 서비스
- [x] Entity 수정 (udt 필드 추가)
- [x] Repository 메서드 추가
- [x] 컴파일 검증

## 미완료 항목

- [ ] 지역 정보 업데이트 (타임존 변경)
- [ ] 성인 전환 기능
- [ ] SNS 계정 추가 연결
- [ ] 관리자 회원 관리 API
- [ ] 통합 테스트
