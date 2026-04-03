@echo off
:: ============================================================
:: SmartExam — Full Deployment Script
:: Tomcat: C:\New folder\apache-tomcat-9.0.115
:: ============================================================
setlocal EnableDelayedExpansion

set TOMCAT_DIR=C:\New folder\apache-tomcat-9.0.115
set PROJECT_DIR=%~dp0
set APP_NAME=SmartExamSystem
set APP_DIR=%TOMCAT_DIR%\webapps\%APP_NAME%
set JAVA_SRC=%PROJECT_DIR%src

echo.
echo  =====================================================
echo   SmartExam -- Full Deployment Script
echo  =====================================================
echo.

:: ── 1. Verify prerequisites ──────────────────────────────
echo [1/6] Checking prerequisites...

if not exist "%TOMCAT_DIR%" (
    echo  [ERROR] Tomcat not found at: %TOMCAT_DIR%
    echo  Update the TOMCAT_DIR variable in this script.
    pause & exit /b 1
)

where javac >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo  [ERROR] javac not found. Make sure JDK 17 is installed and in your PATH.
    pause & exit /b 1
)

if not exist "%PROJECT_DIR%WEB-INF\lib\mysql-connector-j.jar" (
    echo  [WARNING] mysql-connector-j.jar not found in WEB-INF\lib\
    echo  Download it from: https://dev.mysql.com/downloads/connector/j/
    echo  Then place it in: %PROJECT_DIR%WEB-INF\lib\
    pause & exit /b 1
)

echo  OK - All prerequisites met.

:: ── 2. Clean old deployment ──────────────────────────────
echo.
echo [2/6] Cleaning old deployment...
if exist "%APP_DIR%" (
    rmdir /s /q "%APP_DIR%"
    echo  Removed old deployment.
)
mkdir "%APP_DIR%"
mkdir "%APP_DIR%\WEB-INF\classes"
mkdir "%APP_DIR%\WEB-INF\lib"
echo  Created fresh app directory.

:: ── 3. Compile Java source ───────────────────────────────
echo.
echo [3/6] Compiling Java source files...
javac -d "%APP_DIR%\WEB-INF\classes" ^
      -cp "%TOMCAT_DIR%\lib\servlet-api.jar;%PROJECT_DIR%WEB-INF\lib\mysql-connector-j.jar" ^
      -encoding UTF-8 ^
      "%JAVA_SRC%\database\*.java" ^
      "%JAVA_SRC%\model\*.java" ^
      "%JAVA_SRC%\service\*.java" ^
      "%JAVA_SRC%\controller\*.java"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo  [ERROR] Compilation failed. Fix errors above then re-run.
    pause & exit /b 1
)
echo  Compilation successful!

:: ── 4. Copy web resources ────────────────────────────────
echo.
echo [4/6] Deploying web resources...
:: Copy web/ contents directly into the app root (pages, css, js go to app root level)
xcopy /s /e /y "%PROJECT_DIR%web\*" "%APP_DIR%\"
echo  Web files deployed.

:: ── 5. Copy config and libraries ─────────────────────────
echo.
echo [5/6] Deploying WEB-INF config and libraries...
copy /y "%PROJECT_DIR%WEB-INF\web.xml" "%APP_DIR%\WEB-INF\" >nul
copy /y "%PROJECT_DIR%WEB-INF\lib\mysql-connector-j.jar" "%APP_DIR%\WEB-INF\lib\" >nul
echo  Config deployed.

:: ── 6. Done! ─────────────────────────────────────────────
echo.
echo [6/6] Deployment complete!
echo.
echo  =====================================================
echo   NEXT STEPS:
echo   1. Ensure MySQL is running and database is set up
echo      (run: mysql -u root -p ^< db\schema.sql)
echo   2. Restart Tomcat:
echo      - Shutdown: "%TOMCAT_DIR%\bin\shutdown.bat"
echo      - Start:    "%TOMCAT_DIR%\bin\startup.bat"
echo   3. Wait for startup, then open your browser:
echo.
echo    http://localhost:8080/SmartExamSystem/pages/login.html
echo  =====================================================
echo.
pause
