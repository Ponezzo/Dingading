#Requires -Version 5.1
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $ProjectRoot

Write-Host "=== Dingading 로컬 의존성 설치 ===" -ForegroundColor Cyan

$packages = @(
    @{ Id = "EclipseAdoptium.Temurin.17.JDK"; Name = "Java 17" },
    @{ Id = "OpenJS.NodeJS.LTS"; Name = "Node.js LTS" },
    @{ Id = "Python.Python.3.11"; Name = "Python 3.11" },
    @{ Id = "Oracle.MySQL"; Name = "MySQL" },
    @{ Id = "MongoDB.Server"; Name = "MongoDB" },
    @{ Id = "Memurai.MemuraiDeveloper"; Name = "Redis (Memurai)" },
    @{ Id = "MinIO.MinIO"; Name = "MinIO" }
)

foreach ($pkg in $packages) {
    Write-Host "`n>> $($pkg.Name) 설치 확인..." -ForegroundColor Yellow
    winget install $pkg.Id --accept-package-agreements --accept-source-agreements --disable-interactivity 2>&1 | Out-Null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   $($pkg.Name): 이미 설치됨 또는 수동 설치 필요" -ForegroundColor DarkYellow
    } else {
        Write-Host "   $($pkg.Name): 설치 완료" -ForegroundColor Green
    }
}

Write-Host "`n>> RabbitMQ (Erlang 필요) - 수동 설치 권장:" -ForegroundColor Yellow
Write-Host "   https://www.rabbitmq.com/docs/install-windows" -ForegroundColor Gray

Write-Host "`n>> 환경 변수 파일 생성..." -ForegroundColor Yellow
$envDev = Join-Path $ProjectRoot "infra\env\.env.development"
$envExample = Join-Path $ProjectRoot "infra\env\.env.development.example"
if (-not (Test-Path $envDev)) {
    Copy-Item $envExample $envDev
    Write-Host "   infra/env/.env.development 생성됨" -ForegroundColor Green
}

$feEnv = Join-Path $ProjectRoot "frontend\.env.development"
$feExample = Join-Path $ProjectRoot "frontend\.env.development.example"
if (-not (Test-Path $feEnv)) {
    Copy-Item $feExample $feEnv
    Write-Host "   frontend/.env.development 생성됨" -ForegroundColor Green
}

Write-Host "`n>> codegen 도구 npm 설치..." -ForegroundColor Yellow
Set-Location (Join-Path $ProjectRoot "tools")
npm install

Write-Host "`n>> Python 가상환경 및 AI 패키지..." -ForegroundColor Yellow
Set-Location $ProjectRoot
$venvPath = Join-Path $ProjectRoot "ai\venv"
if (-not (Test-Path $venvPath)) {
    python -m venv $venvPath
}
& "$venvPath\Scripts\python.exe" -m pip install --upgrade pip
& "$venvPath\Scripts\pip.exe" install -r (Join-Path $ProjectRoot "ai\requirements.txt")

Write-Host "`n>> 프론트엔드 npm install..." -ForegroundColor Yellow
Set-Location (Join-Path $ProjectRoot "frontend")
npm install

Write-Host "`n=== 설치 결과 참고 ===" -ForegroundColor Cyan
Write-Host "  - MySQL: winget 설치 완료"
Write-Host "  - MongoDB: 설치 후 PC 재시작이 필요할 수 있음"
Write-Host "  - Redis(Memurai): 설치 실패 시 PC 재시작 후 다시 시도하거나"
Write-Host "    https://www.memurai.com/get-memurai 에서 수동 설치"
Write-Host "  - RabbitMQ: https://www.rabbitmq.com/docs/install-windows"

Write-Host "`n=== 설치 완료 ===" -ForegroundColor Green
Write-Host "다음 단계:" -ForegroundColor Cyan
Write-Host "  1. MySQL, MongoDB, Memurai(Redis), RabbitMQ, MinIO 서비스 실행"
Write-Host "  2. .\scripts\codegen.ps1"
Write-Host "  3. .\scripts\dev-backend.ps1  (터미널 1)"
Write-Host "  4. .\scripts\dev-frontend.ps1 (터미널 2)"
Write-Host "  5. .\scripts\dev-ai.ps1       (터미널 3, 선택)"
Write-Host "  6. .\scripts\dev-celery.ps1   (터미널 4, 선택)"
