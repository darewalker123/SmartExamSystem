@echo off
setlocal EnableDelayedExpansion

set TOMCAT_DIR=C:\Tomcat
set PROJECT_DIR=%~dp0
set APP_NAME=SmartExamSystem
set WAR_FILE=%PROJECT_DIR%target\%APP_NAME%.war

echo.
echo SmartExamSystem deployment
echo.

if not exist "%TOMCAT_DIR%\webapps" (
  echo [ERROR] Tomcat webapps folder not found at "%TOMCAT_DIR%\webapps".
  echo Edit TOMCAT_DIR in FULL_DEPLOY.bat to match your Tomcat 9 installation.
  exit /b 1
)

where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
  echo [1/3] Building WAR with Maven...
  call mvn clean package
  if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%
) else (
  echo [1/3] Maven not found. Running compile-only verification.
  call compile.bat
  echo [ERROR] Maven is required to package the WAR automatically.
  echo Install Maven, then re-run FULL_DEPLOY.bat, or package the WAR from an IDE.
  exit /b 1
)

echo [2/3] Deploying WAR...
copy /y "%WAR_FILE%" "%TOMCAT_DIR%\webapps\%APP_NAME%.war" >nul
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

echo [3/3] Done.
echo Start Tomcat and open:
echo http://localhost:8080/SmartExamSystem/pages/login.html
