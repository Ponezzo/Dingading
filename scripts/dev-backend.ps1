#Requires -Version 5.1
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot

$envFile = Join-Path $ProjectRoot "infra\env\.env.development"
if (-not (Test-Path $envFile)) {
    Copy-Item (Join-Path $ProjectRoot "infra\env\.env.development.example") $envFile
    Write-Host ".env.development 파일을 생성했습니다. 필요 시 값을 수정하세요." -ForegroundColor Yellow
}

$generated = Join-Path $ProjectRoot "backend\generated\src\main\java"
if (-not (Test-Path $generated)) {
    Write-Host "generated 코드가 없습니다. 먼저 .\scripts\codegen.ps1 를 실행하세요." -ForegroundColor Red
    exit 1
}

Set-Location (Join-Path $ProjectRoot "backend")
Write-Host "=== Spring Boot 백엔드 시작 (http://localhost:8080) ===" -ForegroundColor Cyan
.\gradlew.bat bootRun --no-daemon
