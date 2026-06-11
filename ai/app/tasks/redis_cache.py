import os
import redis
import json
from dotenv import load_dotenv
from pathlib import Path

# ✅ 1. 로컬 메모리 캐시 초기화
_feature_cache = {}

# ✅ 2. 환경 변수 로드
current_dir = Path(__file__).resolve()
project_root = current_dir.parents[3]  # /app/ai/app/tasks → /app

# 환경 변수 파일 경로 설정
env_mode = os.getenv("ENV_MODE", "development")
env_path = project_root / "infra" / "env" / f".env.{env_mode}"

load_dotenv(dotenv_path=env_path)


# raw_env_mode = os.getenv("ENV_MODE", "development")
# env_mode = raw_env_mode.strip().lower().replace(".local", "").strip(".")

# ✅ 3. 프로젝트 루트 지정
# PROJECT_ROOT
# if os.getenv("PROJECT_ROOT"):
#     project_root = Path(os.getenv("PROJECT_ROOT"))
# else:
#     project_root = Path(__file__).resolve().parents[3]

# ✅ 4. .env 파일 경로 구성
env_file = project_root / "infra" / "env" / f".env.{env_mode}"

# ✅ 5. 디버깅 출력
print(f"🌍 ENV_MODE: {env_mode} → 정제 후: {env_mode}")
print(f"🔍 ENV 파일 경로: {env_file}")

# ✅ 6. 로딩 시도
if env_file.exists():
    load_dotenv(dotenv_path=env_file, override=True)
    print("✅ .env 파일 로드 완료")
else:
    print("⚠️ .env 파일 없음! 기본값 또는 시스템 환경변수 사용 중")

# ✅ 공통 환경 변수
REDIS_HOST = os.getenv("REDIS_HOST", "localhost")
REDIS_PORT = int(os.getenv("REDIS_PORT", 6379))
REDIS_DB = int(os.getenv("REDIS_DB", 0))

# ✅ production용 추가 옵션
redis_options = {
    "host": REDIS_HOST,
    "port": REDIS_PORT,
    "db": REDIS_DB,
    "decode_responses": True,
}

if env_mode == "production":
    REDIS_PASSWORD = os.getenv("REDIS_PASSWORD")

    redis_options.update({
        "password": REDIS_PASSWORD,
    })

# ✅ Redis 클라이언트 생성
redis_client = redis.StrictRedis(**redis_options)
# ✅ Redis 캐싱
def get_cache_feature(cache_key:str):
    print(f"🔎 [get_cache_feature] 요청: {cache_key}")
    # 1. 로컬 캐시
    if cache_key in _feature_cache:
        print(f"🧠 (local) 캐시 HIT: {cache_key}")
        print(f"🧠 Local 값 type: {type(_feature_cache[cache_key])}")
        return _feature_cache[cache_key]

    # 2. Redis 캐시
    print(f"🔎 [Redis] {cache_key} 조회 시도")
    try:
        redis_client.ping()
        print(f"✅ Redis 연결 확인됨 {REDIS_HOST}:{REDIS_PORT}")
        print(f"✅ ENV_MODE={env_mode} | Loaded env from: {env_file}")
    except Exception as e:
        print(f"❌ Redis 연결 실패: {e}")
    redis_val = redis_client.get(cache_key)
    print(f"🔎 [Redis] {cache_key} 조회")
    if redis_val is not None:
        try:
            print(f"🗃️ Redis 값: {redis_val[:200]}")
            data = json.loads(redis_val)
            print(f"🗃️ Redis 값 type: {type(data)} | 내용 일부: {str(data)[:200]}")
            if isinstance(data, dict):
                print(f"🗃️ (redis) 캐시 HIT: {cache_key}")
                _feature_cache[cache_key] = data
                return data
            else:
                print(f"❗ Redis에 저장된 값이 dict 아님: {type(data)}")
        except Exception as e:
            print(f"❌ Redis JSON decode 실패: {e}")
    else:
        print(f"🚫 [Redis MISS] {cache_key}")
    return None

def set_cache_feature(cache_key: str, data: dict, ttl_seconds=3600):
    print(f"📥 [set_cache_feature] 저장 시도: {cache_key}")
    print(f"📥 저장할 데이터 타입: {type(data)} | 일부 내용: {str(data)[:200]}")
    
    _feature_cache[cache_key] = data
    try:
        redis_client.set(cache_key, json.dumps(data))
        print(f"✅ Redis 저장 성공: {cache_key} | TTL: {ttl_seconds}초")
    except Exception as e:
        print(f"❌ Redis 저장 실패: {e}")