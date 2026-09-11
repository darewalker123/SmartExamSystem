# SmartExamSystem

SmartExamSystem is a Smart Online Examination Management System built with Java Servlets, JDBC, MySQL, and a responsive HTML/CSS/JavaScript frontend. It supports three roles: Admin, Teacher, and Student.

## Tech Stack

- Java 17
- Java Servlets using the `javax.servlet` API for Apache Tomcat 9
- JDBC with MySQL Connector/J
- MySQL 8+
- HTML5, CSS3, JavaScript ES6
- Maven WAR build

## Architecture

```text
Browser
  -> HTML/CSS/JavaScript
  -> Servlet Controllers
  -> Service Layer
  -> DAO/JDBC Layer
  -> MySQL
```

Important directories:

- `src/main/java/com/smartexam/model`: User, Exam, Question, Result models
- `src/main/java/com/smartexam/dao`: JDBC data access classes
- `src/main/java/com/smartexam/service`: authentication, exam, scoring, and result workflows
- `src/main/java/com/smartexam/controller`: servlet controllers
- `src/main/java/com/smartexam/util`: DB config, password hashing, sessions, validation, JSON helpers
- `src/main/webapp/pages`: frontend pages
- `src/main/webapp/assets`: CSS and JavaScript
- `db/schema.sql`: MySQL schema and demo data

## Database Setup

1. Start MySQL 8+.
2. Run:

```sql
SOURCE path/to/SmartExamSystem/db/schema.sql;
```

The script creates `smart_exam_system`, all tables, constraints, indexes, demo users, demo exams, and sample MCQ questions.

## Configuration

Configure database credentials with environment variables:

```text
DB_HOST=localhost
DB_PORT=3306
DB_NAME=smart_exam_system
DB_USER=root
DB_PASSWORD=your_password
```

Alternatively, copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties` and edit the values. Do not commit real passwords.

## Build

Preferred build:

```bash
mvn clean test package
```

This creates:

```text
target/SmartExamSystem.war
```

Local Windows compile helper:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/compile-local.ps1
```

Package a WAR without Maven:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/package-war.ps1
```

## Run on Tomcat

1. Build the WAR with Maven.
2. Copy `target/SmartExamSystem.war` to Tomcat 9 `webapps`.
3. Start Tomcat.
4. Open:

```text
http://localhost:8080/SmartExamSystem/pages/login.html
```

You can also set `TOMCAT_DIR` and run:

```cmd
FULL_DEPLOY.bat
```

## Demo Accounts

These accounts are created by `db/schema.sql`:

| Role | Email | Password |
|---|---|---|
| Admin | admin@exam.com | admin123 |
| Teacher | teacher@exam.com | teacher123 |
| Student | student@exam.com | student123 |

Passwords are stored as PBKDF2 hashes in the database seed data.

## Main Features

- Student registration, login, logout
- Role-based protected pages and API authorization
- Student dashboard with active exams and previous results
- Exam-taking UI with MCQ choices, question navigation, countdown timer, and auto-submit
- Server-side score calculation and result persistence
- Duplicate submission prevention
- Teacher exam creation, question creation, exam results, and leaderboard views
- Admin user management and exam oversight
- MySQL schema with primary keys, foreign keys, constraints, indexes, and demo data

## Tests and Verification

Run Maven tests when Maven is installed:

```bash
mvn test
```

The repository also includes a no-dependency scoring smoke test for machines without Maven:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run-smoke-tests.ps1
```

Full browser workflow testing requires MySQL and Tomcat running locally.
