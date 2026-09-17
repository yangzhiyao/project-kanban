@echo off
chcp 65001 >nul
title Kanban Frontend (Angular :4200)
cd /d "%~dp0frontend"

echo ============================================
echo  Kanban Frontend  -  http://localhost:4200
echo ============================================
echo.

where npm >nul 2>nul
if errorlevel 1 (
    echo [ERROR] npm was not found in PATH. Install Node.js first.
    pause
    exit /b 1
)

if not exist "node_modules" (
    echo node_modules not found, running npm install ...
    call npm install
    if errorlevel 1 (
        echo [ERROR] npm install failed.
        pause
        exit /b 1
    )
)

call npm start
set "EXITCODE=%ERRORLEVEL%"

echo.
echo Frontend exited with code %EXITCODE%.
pause
exit /b %EXITCODE%
