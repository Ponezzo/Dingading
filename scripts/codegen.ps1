#Requires -Version 5.1
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $ProjectRoot

Write-Host "=== OpenAPI 코드 생성 ===" -ForegroundColor Cyan

$generatedBackend = Join-Path $ProjectRoot "backend\generated\src\main\java\com\mickey\dinggading"
if (Test-Path $generatedBackend) {
    Get-ChildItem (Join-Path $generatedBackend "api") -Filter "*.java" -ErrorAction SilentlyContinue | Remove-Item -Force
    Get-ChildItem (Join-Path $generatedBackend "model") -Filter "*.java" -ErrorAction SilentlyContinue | Remove-Item -Force
}

$generatedFrontend = Join-Path $ProjectRoot "frontend\generated"
if (Test-Path $generatedFrontend) {
    Get-ChildItem $generatedFrontend -Recurse -Include "*.ts" -ErrorAction SilentlyContinue | Remove-Item -Force
}

$outputDir = Join-Path $ProjectRoot "docs\output"
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

Set-Location (Join-Path $ProjectRoot "tools")
if (-not (Test-Path "node_modules")) {
    npm install
}

npm run codegen
if ($LASTEXITCODE -ne 0) {
    Write-Host "코드 생성 실패" -ForegroundColor Red
    exit 1
}

Write-Host "=== 코드 생성 완료 ===" -ForegroundColor Green
