#Requires -Version 5.1
$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot

$envFile = Join-Path $ProjectRoot "frontend\.env.development"
if (-not (Test-Path $envFile)) {
    Copy-Item (Join-Path $ProjectRoot "frontend\.env.development.example") $envFile
}

Set-Location (Join-Path $ProjectRoot "frontend")
if (-not (Test-Path "node_modules")) {
    npm install
}

Write-Host "=== Next.js 프론트엔드 시작 (http://localhost:3000) ===" -ForegroundColor Cyan
npm run dev
