@echo off
setlocal

:: 1. Create Required Folders
if not exist "WEB-INF\classes" mkdir "WEB-INF\classes"
if not exist "WEB-INF\lib"     mkdir "WEB-INF\lib"

echo [1/3] Folders created: WEB-INF\classes and WEB-INF\lib.
echo [2/3] Tip: Please download 'mysql-connector-j.jar' and place it in WEB-INF\lib.

:: 2. Find Tomcat
set TOMCAT_LIB="C:\Tomcat\lib\servlet-api.jar"
if not exist %TOMCAT_LIB% (
    echo [WARNING] Tomcat folder not found at C:\Tomcat.
    set /p TOMCAT_PATH="Please enter your Tomcat's 'lib' folder path: "
) else (
    set TOMCAT_PATH="C:\Tomcat\lib"
)

:: 3. Compile Command Hint
echo [3/3] To compile your code manually, run this command:
echo javac -d WEB-INF\classes -cp "%TOMCAT_PATH%\servlet-api.jar;WEB-INF\lib\*" src\database\*.java src\model\*.java src\service\*.java src\controller\*.java

echo.
echo Once compiled, copy this entire folder to Tomcat's 'webapps' directory.
pause
