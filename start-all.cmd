@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo Starting Kanban backend and frontend in separate windows ...
echo   Backend  : http://localhost:8080  (API / H2 console)
echo   Frontend : http://localhost:4200
echo.

start "Kanban Backend" cmd /k call "%~dp0start-backend.cmd"
start "Kanban Frontend" cmd /k call "%~dp0start-frontend.cmd"

echo Both services are starting. Close this window when done.
timeout /t 5 >nul
exit /b 0
