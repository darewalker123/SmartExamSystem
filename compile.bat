@echo off
setlocal

where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
  echo Building SmartExamSystem with Maven...
  mvn clean package
  exit /b %ERRORLEVEL%
)

echo Maven was not found. Running local javac compile helper instead.
powershell -ExecutionPolicy Bypass -File scripts\compile-local.ps1
exit /b %ERRORLEVEL%
