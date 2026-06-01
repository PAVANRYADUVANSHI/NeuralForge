@echo off
title NeuralForge - START
color 0A

echo.
echo  ███╗   ██╗███████╗██╗   ██╗██████╗  █████╗ ██╗     ███████╗ ██████╗ ██████╗  ██████╗ ███████╗
echo  ████╗  ██║██╔════╝██║   ██║██╔══██╗██╔══██╗██║     ██╔════╝██╔═══██╗██╔══██╗██╔════╝ ██╔════╝
echo  ██╔██╗ ██║█████╗  ██║   ██║██████╔╝███████║██║     █████╗  ██║   ██║██████╔╝██║  ███╗█████╗
echo  ██║╚██╗██║██╔══╝  ██║   ██║██╔══██╗██╔══██║██║     ██╔══╝  ██║   ██║██╔══██╗██║   ██║██╔══╝
echo  ██║ ╚████║███████╗╚██████╔╝██║  ██║██║  ██║███████╗██║     ╚██████╔╝██║  ██║╚██████╔╝███████╗
echo  ╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝      ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ ╚══════╝
echo.
echo  Autonomous AI Development Intelligence Platform
echo  ================================================
echo.

cd /d "%~dp0"

echo [1/4] Checking Docker...
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo  ERROR: Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)
echo  Docker is running.

echo.
echo [2/4] Checking .env file...
if not exist ".env" (
    echo  WARNING: .env file not found. Copying from .env.example...
    copy ".env.example" ".env" >nul
    echo  .env file created.
)
echo  .env file found.

echo.
echo [3/4] Starting NeuralForge services...
echo  This may take a few minutes on first run (downloading images + building)...
echo.
docker-compose up --build -d

if %errorlevel% neq 0 (
    echo.
    echo  ERROR: Failed to start services. Check docker-compose logs.
    pause
    exit /b 1
)

echo.
echo [4/4] Waiting for services to be healthy...
timeout /t 15 /nobreak >nul

echo.
echo  ============================================================
echo   NeuralForge is RUNNING!
echo  ============================================================
echo.
echo   Frontend   ->  http://localhost:3030
echo   Backend    ->  http://localhost:8081
echo   AI Engine  ->  http://localhost:8091
echo   GraphQL    ->  http://localhost:8081/graphql
echo   Grafana    ->  http://localhost:3031  (admin / neuralforge123)
echo   Prometheus ->  http://localhost:9091
echo.
echo  ============================================================
echo.
echo  Opening NeuralForge in Chrome...

set CHROME1="C:\Program Files\Google\Chrome\Application\chrome.exe"
set CHROME2="C:\Program Files (x86)\Google\Chrome\Application\chrome.exe"

if exist %CHROME1% (
    start "" %CHROME1% http://localhost:3030
) else if exist %CHROME2% (
    start "" %CHROME2% http://localhost:3030
) else (
    echo  Chrome not found. Opening with default browser...
    start "" http://localhost:3030
)

echo.
echo  NeuralForge is running in the background.
echo  Run STOP-NEURALFORGE.bat to stop all services.
echo.
pause
