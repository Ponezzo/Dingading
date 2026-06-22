# Dingading 로컬 개발 가이드

Docker 없이 Windows에서 실행하는 방법입니다.

## 프로젝트 구조

```
.
├── backend/          # Spring Boot
├── frontend/         # Next.js
├── ai/               # FastAPI + Celery
├── docs/             # OpenAPI 스펙
├── generator/        # OpenAPI 커스텀 템플릿
├── infra/env/        # 환경 변수
├── scripts/          # PowerShell 실행 스크립트
└── tools/            # OpenAPI codegen npm 도구
```

## 시스템 요구사항

| 구성요소 | 버전 |
|----------|------|
| Java (Temurin) | 17 |
| Node.js | LTS |
| Python | 3.11 |
| MySQL | 8.x |
| MongoDB | 6.x |
| Redis | 7.x (Windows: Memurai) |
| RabbitMQ | 3.11+ |
| MinIO | latest |

## 1. 필수 프로그램 설치

PowerShell에서 프로젝트 루트 기준:

```powershell
.\scripts\install-deps.ps1
```

설치 대상: Java 17, Node.js LTS, Python 3.11, MySQL, MongoDB, Redis(Memurai), MinIO  
RabbitMQ는 [Windows 설치 가이드](https://www.rabbitmq.com/docs/install-windows) 참고.

## 2. 환경 변수

```powershell
copy infra\env\.env.development.example infra\env\.env.development
copy frontend\.env.development.example frontend\.env.development
```

Google OAuth, OpenVidu 등 외부 서비스 키는 `.env.development`에서 수정.  
자세한 항목은 `infra/env/.env.development.example` 참고.

## 3. OpenAPI 코드 생성

OpenAPI 스펙(`docs/`)이 변경되면:

```powershell
.\scripts\codegen.ps1
```

## 4. 서비스 실행 (터미널별)

**백엔드 없이 FE만 테스트 (Mock 모드):**

```powershell
cd frontend
# .env.development 에 NEXT_PUBLIC_USE_MOCK=true 확인
npm run dev
```

브라우저에서 http://localhost:3000/main 접속. 우하단 `MOCK MODE` 배지가 보이면 Mock 동작 중.

Mock 제거 방법: `frontend/src/mocks/README.md` 참고.

**전체 스택 실행 (터미널별):**

| 터미널 | 명령 | URL |
|--------|------|-----|
| 1 | `.\scripts\dev-backend.ps1` | http://localhost:8080 |
| 2 | `.\scripts\dev-frontend.ps1` | http://localhost:3000 |
| 3 | `.\scripts\dev-ai.ps1` | http://localhost:8000 |
| 4 | `.\scripts\dev-celery.ps1` | (백그라운드 워커) |

실행 전 MySQL, MongoDB, Redis, RabbitMQ, MinIO 서비스가 켜져 있어야 합니다.

### 포트

| 서비스 | 포트 |
|--------|------|
| Spring Boot | 8080 |
| Next.js | 3000 |
| FastAPI | 8000 |
| MySQL | 3306 |
| MongoDB | 27017 |
| Redis | 6379 |
| RabbitMQ | 5672 |
| MinIO API | 9000 |

## 트러블슈팅

1. **백엔드 DB 연결 실패** — MySQL/MongoDB/Redis/RabbitMQ 서비스 실행 여부 확인
2. **generated 코드 없음** — `.\scripts\codegen.ps1` 실행
3. **MinIO 버킷 오류** — MinIO 콘솔(http://localhost:9001)에서 `original-mp3-audio-bucket` 생성
4. **Google 로그인 불가** — `.env.development`의 `GOOGLE_ID`, `GOOGLE_SECRET` 설정 필요
