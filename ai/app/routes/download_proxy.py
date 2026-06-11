# ai/app/routes/download_proxy.py 또는 main.py에 추가 가능

import base64
import requests
import io
from fastapi import APIRouter, HTTPException, Response
from fastapi.responses import StreamingResponse
import argparse
import urllib.parse

import os
MINIO_REPLACEMENT = os.getenv("PROXY_LOCALHOST_REPLACEMENT", "minio:9001")

router = APIRouter()

@router.get("/api/v1/download-shared-object/{encoded_url:path}")
async def download_shared_object(encoded_url: str):
    try:
        # base64 padding 보정
        missing_padding = len(encoded_url) % 4
        if missing_padding:
            encoded_url += "=" * (4 - missing_padding)
        
        # 1. base64 디코딩
        decoded_bytes = base64.urlsafe_b64decode(encoded_url.encode())
        decoded_url = decoded_bytes.decode("utf-8")

        # 💡 localhost → 도커 네트워크용 호스트로 치환
        decoded_url = decoded_url.replace("localhost:9001", MINIO_REPLACEMENT)

        print(f"🌐 요청된 presigned URL: {decoded_url}")

        # 2. 실제 presigned URL로 GET 요청
        response = requests.get(decoded_url)
        print("📥 실제 URL 응답 코드:", response.status_code)
        response.raise_for_status()

        if response.status_code != 200:
            raise HTTPException(status_code=404, detail="파일을 가져올 수 없습니다.")

        response.raise_for_status()

        # 3. 콘텐츠 반환
        return StreamingResponse(io.BytesIO(response.content), media_type="application/octet-stream",
                                 headers={"Content-Disposition": f'attachment; filename="download.wav"'})
    
    except Exception as e:
        print(f"❌ 다운로드 중 에러 발생: {e}")
        raise HTTPException(status_code=500, detail="다운로드 중 오류가 발생했습니다.")
