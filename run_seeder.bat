@echo off
echo Copying MySQL Driver...
copy /y "C:\New folder\apache-tomcat-9.0.115\webapps\SmartExamSystem\WEB-INF\lib\mysql-connector-j-9.5.0.jar" .
if exist mysql-connector-j-9.5.0.jar (
    echo Running Seeder...
    java -cp ".classes;mysql-connector-j-9.5.0.jar" database.LoadTestData
) else (
    echo Failed to copy driver jar!
)
