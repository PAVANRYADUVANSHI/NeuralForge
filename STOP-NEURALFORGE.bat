@echo off
title NeuralForge - STOP
color 0C

echo.
echo  ███╗   ██╗███████╗██╗   ██╗██████╗  █████╗ ██╗     ███████╗ ██████╗ ██████╗  ██████╗ ███████╗
echo  ████╗  ██║██╔════╝██║   ██║██╔══██╗██╔══██╗██║     ██╔════╝██╔═══██╗██╔══██╗██╔════╝ ██╔════╝
echo  ██╔██╗ ██║█████╗  ██║   ██║██████╔╝███████║██║     █████╗  ██║   ██║██████╔╝██║  ███╗█████╗
echo  ██║╚██╗██║██╔══╝  ██║   ██║██╔══██╗██╔══██║██║     ██╔══╝  ██║   ██║██╔══██╗██║   ██║██╔══╝
echo  ██║ ╚████║███████╗╚██████╔╝██║  ██║██║  ██║███████╗██║     ╚██████╔╝██║  ██║╚██████╔╝███████╗
echo  ╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝      ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ ╚══════╝
echo.
echo  Stopping Autonomous AI Development Intelligence Platform
echo  =========================================================
echo.

cd /d "%~dp0"

echo [1/3] Checking Docker...
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo  Docker is not running. Nothing to stop.
    pause
    exit /b 0
)

echo.
set /p CONFIRM="Are you sure you want to stop NeuralForge? (Y/N): "
if /i not "%CONFIRM%"=="Y" (
    echo  Cancelled. NeuralForge is still running.
    pause
    exit /b 0
)

echo.
echo [2/3] Stopping all NeuralForge containers...
docker-compose down

if %errorlevel% neq 0 (
    echo  WARNING: Some containers may not have stopped cleanly.
)

echo.
echo [3/3] Cleaning up...
set /p CLEAN="Remove volumes (database data)? This will DELETE all data! (Y/N): "
if /i "%CLEAN%"=="Y" (
    echo  Removing volumes...
    docker-compose down -v
    echo  Volumes removed.
) else (
    echo  Volumes kept. Your data is safe.
)

echo.
echo  ============================================================
echo   NeuralForge has been STOPPED.
echo  ============================================================
echo.
echo   Run START-NEURALFORGE.bat to start again.
echo.
pause
