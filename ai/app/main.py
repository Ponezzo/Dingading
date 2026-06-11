import os
import json
from pathlib import Path
from dotenv import load_dotenv

from fastapi import FastAPI
from pydantic import BaseModel

import asyncio
from aio_pika import connect_robust, Message, ExchangeType

from ai.app.tasks.audio_analysis import run_audio_analysis

# 환경변수 호출
current_dir = Path(__file__).resolve()
project_root = current_dir.parent.parent.parent  # /app/ai/app → /app
env_mode = os.getenv("ENV_MODE", "development")
env_path = project_root / "infra" / "env" / f".env.{env_mode}"
load_dotenv(dotenv_path=env_path)


# 🌟 FastAPI 인스턴스 생성
app = FastAPI()

# 🔐 RabbitMQ 설정
RABBITMQ_URL = (
    f"amqp://{os.getenv('RABBITMQ_DEFAULT_USER')}:{os.getenv('RABBITMQ_DEFAULT_PASS')}"
    f"@{os.getenv('RABBIT_MQ_HOST')}:{os.getenv('RABBIT_MQ_PORT')}/"
)
REQUEST_EXCHANGE_NAME = "audio-analysis-request-exchange"
RESPONSE_EXCHANGE_NAME = "audio-analysis-response-exchange"
REQUEST_QUEUE_NAME = "audio-analysis-request"
RESPONSE_ROUTING_KEY = "audio.analysis.response"

# ✅ 메시지 포맷
class AnalyzeRequest(BaseModel):
    recordFilename: str
    originalFilename: str
    attemptId : int

class AnalyzeResponse(BaseModel):
    attemptId : int
    beatScore: float
    tuneScore: float
    toneScore: float

# RabbitMQ 연결 설정정
@app.on_event("startup")
async def startup_event():
    print("✅ startup_event 실행됨")
    loop = asyncio.get_event_loop()
    loop.create_task(start_rabbitmq_consumer())


# RabbitMQ 소비자 시작
async def start_rabbitmq_consumer():
    connection = await connect_robust(RABBITMQ_URL)
    channel = await connection.channel()
    await channel.set_qos(prefetch_count=4)

    # 요청 큐 바인딩
    request_exchange = await channel.declare_exchange(
        REQUEST_EXCHANGE_NAME,
        ExchangeType.DIRECT,
        durable=True
        )
    queue = await channel.declare_queue(
        REQUEST_QUEUE_NAME,
        durable=True,
        arguments={
            "x-message-ttl": 60000,
            "x-max-length": 1000
        }
    )    
    await queue.bind(request_exchange, routing_key="audio.analysis.request")

    response_exchange = await channel.declare_exchange(
        RESPONSE_EXCHANGE_NAME,
        ExchangeType.DIRECT,
        durable=True
    )

    active_tasks = {}
    
    async with queue.iterator() as queue_iter:
        async for message in queue_iter:
            try:
                body = message.body.decode()
                data = json.loads(body)
                request = AnalyzeRequest(**data)
                
                task = run_audio_analysis.delay(
                    request.recordFilename,
                    request.originalFilename
                )
                
                active_tasks[task.id] = {
                    "task": task,
                    "request": request,
                    "message": message
                }
                
            except Exception as e:
                await message.ack()
                
    asyncio.create_task(check_task_results(channel, response_exchange, active_tasks))

# 🎯 비동기적으로 태스크 결과 확인 및 응답 발행
async def check_task_results(channel, response_exchange, active_tasks):
    while True:
        task_ids = list(active_tasks.keys())
        for task_id in task_ids:
            task_info = active_tasks[task_id]
            task = task_info["task"]
            
            if task.ready():
                try:
                    result = task.result
                    if not isinstance(result, dict):
                        result = {"tuneScore": 0.0, "toneScore": 0.0, "beatScore": 0.0}
                    result["attemptId"] = task_info["request"].attemptId
                    
                    await response_exchange.publish(
                        Message(body=json.dumps(result).encode()),
                        routing_key=RESPONSE_ROUTING_KEY
                    )
                except Exception:
                    pass
                finally:
                    await task_info["message"].ack()
                    del active_tasks[task_id]
        
        await asyncio.sleep(0.1)

# ✅ 헬스체크
@app.get("/health/")
def health_check():
    print(run_audio_analysis)
    return {"status": "OK"}
