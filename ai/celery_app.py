# 파일: ai/celery_app.py
from celery import Celery
import os
from dotenv import load_dotenv
from pathlib import Path

# .env 경로 설정
current_dir = Path(__file__).resolve()
project_root = current_dir.parent.parent  # /app/ai → /app

# 🌱 환경 변수 로드
env_mode = os.getenv("ENV_MODE", "development")
env_path = project_root / "infra" / "env" / f".env.{env_mode}"

if env_path.exists():
    load_dotenv(dotenv_path=env_path)
    print(f"✅ .env 파일 로드 성공: {env_path}")
else:
    print(f"⚠️ .env 파일 없음: {env_path}, 시스템 환경변수 사용")

# Celery 인스턴스 생성
celery = Celery(
    "tasks",
    broker=os.getenv("CELERY_BROKER_URL", "redis://:1234@redis:6379"),
    backend=os.getenv("CELERY_RESULT_BACKEND", "redis://:1234@redis:6379")
)

# tasks 디렉토리 자동 탐색
celery.autodiscover_tasks(["ai.app.tasks"])

# 로깅용 확인
print("✅ Celery 인스턴스 생성 및 자동탐색 완료")