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

## 환경 변수

- `infra/env/.env.development.example` → `.env.development`로 복사
- `frontend/.env.development.example` → `.env.development`로 복사

## OpenAPI 코드 생성

OpenAPI 스펙(`docs/`)이 변경되면:

```powershell
.\scripts\codegen.ps1
```

## 트러블슈팅

1. **백엔드 DB 연결 실패** — MySQL/MongoDB/Redis/RabbitMQ 서비스 실행 여부 확인
2. **generated 코드 없음** — `.\scripts\codegen.ps1` 실행
3. **MinIO 버킷 오류** — MinIO 콘솔(http://localhost:9001)에서 `original-mp3-audio-bucket` 생성
4. **Google 로그인 불가** — `.env.development`의 `GOOGLE_ID`, `GOOGLE_SECRET` 설정 필요
