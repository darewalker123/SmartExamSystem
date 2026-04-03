# 🚀 SmartExam — Deployment Guide

> Updated deployment guide with the **correct URL** and **step-by-step deployment**.

---

## ✅ Prerequisites

| Requirement | Details |
|---|---|
| Java JDK 17+ | In your PATH (`java -version` to check) |
| MySQL Server 8.0+ | Running, with `smart_exam_system` database |
| Apache Tomcat 9.x | At `C:\New folder\apache-tomcat-9.0.115` |
| `mysql-connector-j.jar` | Must be in `WEB-INF\lib\` |

---

## 📦 Step 1: Get MySQL Connector JAR

If you don't have the JAR file yet, run this helper script:
```
double-click: download-connector.bat
```
This will auto-download `mysql-connector-j.jar` into `WEB-INF\lib\`.

---

## 🗄️ Step 2: Setup Database

Open **MySQL Command Line** or **MySQL Workbench** and run:
```sql
SOURCE C:/Users/prath/.gemini/antigravity/scratch/SmartExamSystem/db/schema.sql;
```

Then add a test admin user (optional):
```sql
USE smart_exam_system;
INSERT INTO users (name, email, password, role)
VALUES ('Admin', 'admin@exam.com', 'admin123', 'admin');
```

> **Check credentials** in `src/database/DatabaseUtil.java`:
> Currently set to `root` / `prathyush`. Change if needed.

---

## ⚙️ Step 3: Compile & Deploy

Double-click `FULL_DEPLOY.bat` or run in CMD:
```cmd
cd "C:\Users\prath\.gemini\antigravity\scratch\SmartExamSystem"
FULL_DEPLOY.bat
```

The script will:
1. Validate all prerequisites
2. Compile Java source files
3. Copy web pages to Tomcat webapps
4. Copy WEB-INF config + JAR

---

## 🌐 Step 4: Start Tomcat & Open Browser

Start Tomcat (if not already running):
```cmd
"C:\New folder\apache-tomcat-9.0.115\bin\startup.bat"
```

Wait for: `Server startup in [X] ms`

Then open in your browser:

### 👉 **http://localhost:8080/SmartExamSystem/pages/login.html**

---

## 🔑 Default Login Credentials

| Role | Email | Password |
|---|---|---|
| Admin | admin@exam.com | admin123 |
| Teacher | Register via UI | your password |
| Student | Register via UI | your password |

---

## 🛠️ Troubleshooting

| Error | Fix |
|---|---|
| **404 Not Found** | Re-run `FULL_DEPLOY.bat`. The URL is `/SmartExamSystem/pages/login.html` |
| **DB Connection Error** | Ensure MySQL is running. Check credentials in `DatabaseUtil.java` |
| **Compilation Error** | Ensure JDK 17 is in PATH. Run `javac -version` in CMD |
| **Tomcat not found** | Edit `TOMCAT_DIR` in `FULL_DEPLOY.bat` to match your Tomcat path |
| **mysql-connector missing** | Run `download-connector.bat` first |
