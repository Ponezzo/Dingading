#Requires -Version 5.1
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$env:ENV_MODE = "development"
$env:PYTHONPATH = $ProjectRoot

$venvCelery = Join-Path $ProjectRoot "ai\venv\Scripts\celery.exe"
if (-not (Test-Path $venvCelery)) {
    Write-Host "Python venv가 없습니다. .\scripts\install-deps.ps1 를 먼저 실행하세요." -ForegroundColor Red
    exit 1
}

Set-Location $ProjectRoot
Write-Host "=== Celery Worker 시작 ===" -ForegroundColor Cyan
& $venvCelery -A ai.celery_worker:celery worker --loglevel=info --pool=solo
