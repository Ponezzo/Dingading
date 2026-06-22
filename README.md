# 🎸 딩가딩 (Ding-Ga-Ding) 프로젝트

> AI 음성 인식 기반의 연주 실력 측정 및 밴드 구인구직 플랫폼

## 🎬 시연 영상

<p align="center">
  <video src="https://github.com/user-attachments/assets/415d64c5-54c7-4d49-924a-e779e8c9049b" controls playsinline width="900"></video>
</p>

## 로컬 실행 (Docker 없음)

### 1. 필수 프로그램 설치

PowerShell에서 프로젝트 루트 기준:

```powershell
.\scripts\install-deps.ps1
```

설치 대상: Java 17, Node.js LTS, Python 3.11, MySQL, MongoDB, Redis(Memurai), MinIO  
RabbitMQ는 [Windows 설치 가이드](https://www.rabbitmq.com/docs/install-windows) 참고.

### 2. 환경 변수

```powershell
copy infra\env\.env.development.example infra\env\.env.development
copy frontend\.env.development.example frontend\.env.development
```

Google OAuth, OpenVidu 등 외부 서비스 키는 `.env.development`에서 수정.

### 3. OpenAPI 코드 생성

```powershell
.\scripts\codegen.ps1
```

### 4. 서비스 실행 (터미널별)

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

## 기술 스택

- **Backend**: Java 17, Spring Boot 3, Gradle
- **Frontend**: Next.js 15, React 19, Three.js
- **AI**: Python, FastAPI, Celery
- **DB**: MySQL, MongoDB, Redis, RabbitMQ, MinIO

자세한 환경 변수 목록은 `infra/env/.env.development.example` 참고.
