# Smart Online Examination System - Setup

## 1. Prerequisites
- **JDK 17** & **Apache Tomcat 9/10**.
- **MySQL Server** & **mysql-connector-j** jar in `/lib`.

## 2. Setup
1. Execute `db/schema.sql` in MySQL.
2. Update `src/database/DatabaseUtil.java` with your MySQL `USER` and `PASSWORD`.
3. Deploy the `/web` directory and `/src` to Tomcat.

## 3. Access
- Go to `http://localhost:8080/SmartExamSystem/pages/login.html`.
