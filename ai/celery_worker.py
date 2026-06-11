# 파일: celery_worker.py (루트 위치)
import os
import sys
from dotenv import load_dotenv
from pathlib import Path

# 현재 파일(ai/celery_worker.py) 기준으로 루트 경로 계산
current_dir = Path(__file__).resolve()
project_root = current_dir.parent.parent  # /app/ai → /app

# 환경 변수 파일 경로 설정
env_mode = os.getenv("ENV_MODE", "development")
env_path = project_root / "infra" / "env" / f".env.{env_mode}"

if env_path.exists():
    load_dotenv(dotenv_path=env_path)
    print(f"✅ .env 파일 로드 성공: {env_path}")
else:
    print(f"⚠️ .env 파일 없음: {env_path}, 시스템 환경변수 사용")

# ✅ Celery 인스턴스 로딩
print("🚀 Celery 인스턴스 불러오는 중...")
from ai.celery_app import celery  # 🎯 핵심: 이 인스턴스를 Celery CLI가 인식

# 👇 실제로 task 파일 import해서 등록되게 해야 함!
import ai.app.tasks.audio_analysis  # 반드시 있어야 Celery가 task를 인식함!


# ✅ 디버깅용: 등록된 태스크 확인
def show_registered_tasks():
    print("📋 등록된 Celery 태스크 목록:")
    for name in celery.tasks.keys():
        print(f" - {name}")

if __name__ == "__main__":
    show_registered_tasks()
