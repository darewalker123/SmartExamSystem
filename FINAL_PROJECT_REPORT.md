# Final Project Report: Smart Online Examination Management System

## What Was Missing

The repository contained documentation, deployment notes, generated helper scripts, a connector jar, and crash logs, but it did not contain the runnable Java Servlet application source tree, web pages, or database schema described by the documentation.

## What Was Reconstructed

- Maven WAR project configuration
- Java model, DAO, service, controller, and utility layers
- MySQL schema with constraints, indexes, and demo data
- Responsive HTML/CSS/JavaScript frontend
- Student, Teacher, and Admin workflows
- Server-side exam scoring and duplicate-submission protection
- Updated README, offline deployment guide, and compile/deploy scripts

## Architecture

```text
Browser
  -> HTML/CSS/JavaScript
  -> Servlet Controllers
  -> Service Layer
  -> DAO/JDBC Layer
  -> MySQL
```

## Main Features

- Public login and registration
- PBKDF2 password hashing
- HTTP session authentication
- Role-based API protection
- Student exam list, exam attempt UI, timer, auto-submit, result view, previous results, leaderboard
- Teacher exam creation, question creation, results, and leaderboard views
- Admin user management and exam oversight

## Database Tables

- `users`
- `exams`
- `questions`
- `results`

The schema is available in `db/schema.sql`.

## Build

```bash
mvn clean test package
```

Without Maven on Windows:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run-smoke-tests.ps1
```

## Run

1. Run `db/schema.sql` in MySQL.
2. Set `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, and `DB_PASSWORD`.
3. Build `target/SmartExamSystem.war`.
4. Deploy it to Apache Tomcat 9.
5. Open `http://localhost:8080/SmartExamSystem/pages/login.html`.

## Demo Accounts

| Role | Email | Password |
|---|---|---|
| Admin | admin@exam.com | admin123 |
| Teacher | teacher@exam.com | teacher123 |
| Student | student@exam.com | student123 |

## Verification Performed

- Local Java source compilation through `scripts/compile-local.ps1`
- Scoring smoke test through `scripts/run-smoke-tests.ps1`
- JavaScript syntax check with `node --check src/main/webapp/assets/js/app.js`

MySQL and Tomcat were not installed/on PATH in the local environment, so live database initialization and full browser/Tomcat workflow testing must be run on a machine with those services available.
