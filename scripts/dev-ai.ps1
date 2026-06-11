#Requires -Version 5.1
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$env:ENV_MODE = "development"
$env:PYTHONPATH = $ProjectRoot

$venvPython = Join-Path $ProjectRoot "ai\venv\Scripts\python.exe"
if (-not (Test-Path $venvPython)) {
    Write-Host "Python venv가 없습니다. .\scripts\install-deps.ps1 를 먼저 실행하세요." -ForegroundColor Red
    exit 1
}

Set-Location $ProjectRoot
Write-Host "=== FastAPI AI 서버 시작 (http://localhost:8000) ===" -ForegroundColor Cyan
& $venvPython -m uvicorn ai.app.main:app --host 0.0.0.0 --port 8000 --reload
