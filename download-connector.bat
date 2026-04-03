@echo off
:: Quick helper: download MySQL Connector/J and place it in WEB-INF\lib
echo Downloading mysql-connector-j.jar using PowerShell...
echo.
powershell -Command "& { $url = 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar'; $out = '%~dp0WEB-INF\lib\mysql-connector-j.jar'; Write-Host 'Downloading...'; Invoke-WebRequest -Uri $url -OutFile $out; Write-Host 'Done! Saved to WEB-INF\lib\mysql-connector-j.jar' }"
echo.
echo Now run FULL_DEPLOY.bat to compile and deploy.
pause
