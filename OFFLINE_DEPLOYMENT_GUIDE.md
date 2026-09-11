# SmartExamSystem Offline Deployment Guide

## Prerequisites

- JDK 17+
- MySQL 8+
- Apache Tomcat 9.x
- Maven 3.9+ recommended

This application uses `javax.servlet`, so Tomcat 9 is the intended target. Do not deploy it to Tomcat 10 unless you migrate the code to `jakarta.servlet`.

## Database

Run:

```sql
SOURCE C:/path/to/SmartExamSystem/db/schema.sql;
```

Then configure:

```text
DB_HOST=localhost
DB_PORT=3306
DB_NAME=smart_exam_system
DB_USER=root
DB_PASSWORD=your_password
```

## Build and Deploy

Recommended:

```cmd
mvn clean package
copy target\SmartExamSystem.war C:\path\to\apache-tomcat-9\webapps\
```

Start Tomcat and open:

```text
http://localhost:8080/SmartExamSystem/pages/login.html
```

## Scripted Deployment

Edit `TOMCAT_DIR` in `FULL_DEPLOY.bat`, then run:

```cmd
FULL_DEPLOY.bat
```

## Demo Login

| Role | Email | Password |
|---|---|---|
| Admin | admin@exam.com | admin123 |
| Teacher | teacher@exam.com | teacher123 |
| Student | student@exam.com | student123 |

## Troubleshooting

| Problem | Fix |
|---|---|
| 404 page | Confirm the WAR deployed as `SmartExamSystem.war` and use `/SmartExamSystem/pages/login.html`. |
| Database error | Confirm MySQL is running and DB environment variables match your local credentials. |
| Servlet API mismatch | Use Tomcat 9 for `javax.servlet`; Tomcat 10 uses `jakarta.servlet`. |
| Maven unavailable | Use `scripts/compile-local.ps1` for a basic local compile, or install Maven for WAR packaging. |

To create a WAR without Maven, run:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/package-war.ps1
```
