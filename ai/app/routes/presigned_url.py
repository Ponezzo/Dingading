from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from minio import Minio
from minio.error import S3Error
import os

router = APIRouter()

class PresignedURLRequest(BaseModel):
    object_name: str  # 예: silver_bass_윤도현-나는나비.wav
    bucket_name: str = os.getenv("MINIO_BUCKET", "original-mp3-audio-bucket")
    expires_sec: int = 3600  # URL 유효기간 (기본 1시간)

@router.post("/api/v1/generate-presigned-url")
def generate_presigned_url(req: PresignedURLRequest):
    try:
        # ✅ MinIO 클라이언트 생성
        client = Minio(
            endpoint=os.getenv("MINIO_ENDPOINT"),
            access_key=os.getenv("MINIO_USERNAME"),
            secret_key=os.getenv("MINIO_PASSWORD"),
            secure=False,
        )

        # ✅ presigned URL 발급
        url = client.presigned_get_object(
            bucket_name=req.bucket_name,
            object_name=req.object_name,
            expires=req.expires_sec
        )

        return {"presigned_url": url}

    except S3Error as e:
        print("❌ MinIO 오류:", e)
        raise HTTPException(status_code=500, detail=f"MinIO 오류: {e}")
    except Exception as e:
        print("❌ 알 수 없는 오류:", e)
        raise HTTPException(status_code=500, detail="URL 발급 실패")
