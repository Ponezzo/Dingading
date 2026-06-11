# 로컬용 minio 업로드 python 파일

import os
from minio import Minio
from minio.error import S3Error
from dotenv import load_dotenv

# 환경 변수 로드
load_dotenv(dotenv_path="../../../infra/env/.env.development.local")

print("✅ MINIO_ENDPOINT:", os.getenv("MINIO_ENDPOINT"))  # None이면 문제!

# ✅ MinIO 클라이언트 설정
minio_client = Minio(
    endpoint=os.getenv("MINIO_ENDPOINT"),
    access_key=os.getenv("MINIO_ACCESS_KEY"),
    secret_key=os.getenv("MINIO_SECRET_KEY"),
    secure=False
)

# 버킷 이름
# bucket_name = "images"
bucket_name = os.getenv("MINIO_BUCKET_NAME", "original-audio")

# ✅ 버킷이 없으면 생성
if not minio_client.bucket_exists(bucket_name):
    minio_client.make_bucket(bucket_name)
    print(f"✅ 버킷 생성: {bucket_name}")
else:
    print(f"📦 버킷 존재함: {bucket_name}")

local_base_dir = r"C:/Users/SSAFY/Downloads/DB"  # 윈도우 경로는 r""로 감싸기

# ✅ 재귀적으로 파일 업로드
for root, dirs, files in os.walk(local_base_dir):
    for file in files:
        local_path = os.path.join(root, file)
        # 윈도우 경로 → S3 경로
        relative_path = os.path.relpath(local_path, local_base_dir)
        object_name = relative_path.replace("\\", "/")
        
        # 파일 업로드
        try:
            print(f"🚀 업로드 중: {relative_path}")
            minio_client.fput_object(bucket_name, object_name, local_path)
            print(f"🟢 업로드 성공: {object_name}")
        except S3Error as e:
            print(f"❌ 업로드 실패: {relative_path} → {e}")

print("✅ 업로드 완료!")