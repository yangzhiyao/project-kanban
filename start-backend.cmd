@echo off
chcp 65001 >nul
title Kanban Backend (Spring Boot :8080)
cd /d "%~dp0backend"

echo ============================================
echo  Kanban Backend  -  http://localhost:8080
echo ============================================
echo.

rem Maven's bundled Guice calls sun.misc.Unsafe::staticFieldBase, which JDK 23+
rem flags as terminally deprecated. Silence it only if the JVM understands the flag.
set "JAVA_MAJOR=0"
set "JAVA_VERSION="
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JAVA_VERSION=%%v"
if not defined JAVA_VERSION goto :jvm_opts_done
set "JAVA_VERSION=%JAVA_VERSION:"=%"
for /f "delims=.-+ tokens=1" %%m in ("%JAVA_VERSION%") do set "JAVA_MAJOR=%%m"
if %JAVA_MAJOR% GEQ 23 set "MAVEN_OPTS=--sun-misc-unsafe-memory-access=allow %MAVEN_OPTS%"
:jvm_opts_done

call mvnw.cmd spring-boot:run
set "EXITCODE=%ERRORLEVEL%"

echo.
echo Backend exited with code %EXITCODE%.
pause
exit /b %EXITCODE%
