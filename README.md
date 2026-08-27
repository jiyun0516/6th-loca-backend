# LOCA Backend - 취향 기반 로컬 라이프 서비스

> **대중의 별점이 아닌, ‘내 스타일’로 동네를 디깅하다.**

**Live Demo**: [loca-hongik.vercel.app](https://loca-hongik.vercel.app/)  
**Backend Repository**: [jiyun0516/6th-loca-backend](https://github.com/jiyun0516/6th-loca-backend)

## 프로젝트 소개

LOCA는 넘쳐나는 대중적 정보와 광고성 평점에서 벗어나, 사용자의 취향과 방문 맥락을 바탕으로 홍대 지역의 장소와 새로운 경험을 제안하는 로컬 라이프 서비스입니다.

LOCA Backend는 사용자가 남긴 방문 기록과 태그를 선호도 데이터로 전환하고, 이를 바탕으로 아직 방문하지 않은 장소를 개인화해 추천합니다. 

회원 인증부터 장소, 방문 기록, 장소 목록까지 서비스에 필요한 데이터와 API를 관리합니다.

---

## MVP Scope

| Domain | Implementation |
| --- | --- |
| Authentication | JWT 기반 회원가입·로그인, 사용자·관리자 권한 분리 |
| Place | 공개 장소 조회, 사용자 장소 관리, 소프트 삭제 |
| Visit Record | 방문 정보와 태그 기록, 이미지 업로드, 기록 관리 |
| Explore | 선택한 태그를 기준으로 미방문 장소 탐색 |
| For You | 방문 기록에서 계산한 사용자 선호도 기반 장소 추천 |
| Place List | 사용자별 장소 목록 관리 및 공유 토큰 발급 |

---

## Recommendation

For You 기능은 리뷰가 단순한 기록으로 끝나지 않고 다음 추천에 반영되도록 설계했습니다. 사용자가 리뷰에 남긴 태그를 사용자 취향 점수와 장소별 태그 점수에 반영하고, 두 점수가 잘 맞는 미방문 장소를 추천합니다.

리뷰가 쌓일수록 사용자의 취향이 추천 결과에 지속적으로 반영되도록 했으며, 추천 점수가 동일할 때 응답 순서가 튀지 않도록 보조 정렬 기준도 추가했습니다.

### Preference Model

사용자 선호도는 리뷰에서 선택한 태그의 누적 횟수로 계산합니다.  
장소 선호도는 한 사용자의 반복 기록이 과도하게 반영되지 않도록 해당 태그를 선택한 사용자 수를 기준으로 계산합니다.

```text
Recommendation Score
= Σ(Normalized User Preference × Normalized Place Preference)
```

### Preference Refresh

리뷰가 생성·수정·삭제되면 해당 장소를 재집계 대상으로 표시합니다. 이후 추천 조회 시 변경된 장소의 선호도만 다시 계산합니다.

재집계 대상을 메모리가 아닌 DB에 저장해 서버가 재시작되어도 상태가 유지되도록 했으며, 장소별 트랜잭션과 잠금으로 중복 처리를 방지했습니다.

### Query Optimization

목록 조회에 `Slice`를 적용해 불필요한 Count Query를 줄였습니다. 추천 장소 정보는 한 번에 조회해 N+1 문제를 방지하고, 동점 정렬 기준을 고정해 페이지 간 중복과 누락을 막았습니다.

---

## Tech Stack

| Category | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4.1.0, Spring MVC |
| Database | PostgreSQL, Supabase |
| Security | Spring Security, JWT, BCrypt |
| Storage | Supabase Storage |
| API Documentation | Swagger, Springdoc OpenAPI |
| Build & Deployment | Gradle, Docker, Render |

---

## Project Structure

```text
src/main/java/gdg/hongik/loca
├── config
│   ├── SecurityConfig
│   ├── CorsConfig
│   ├── SwaggerConfig
│   └── SupabaseStorageConfig
│
├── security
│   ├── JwtTokenProvider
│   └── JwtAuthenticationFilter
│
├── controller
│   ├── AuthController
│   ├── PublicPlaceController
│   ├── CustomPlaceController
│   ├── ReviewController
│   ├── RecommendationController
│   └── PlaceListController
│
├── service
│   ├── ReviewService
│   ├── RecommendationService
│   ├── ForYouScoreCalculator
│   ├── UserPreferenceUpdater
│   ├── PlacePreferenceUpdater
│   └── PlacePreferenceRefresher
│
├── repository
├── entity
├── dto
└── exception
```
