path = r"c:\Users\prath\.gemini\antigravity\scratch\SmartExamSystem\src\database\LoadTestData.java"

with open(path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Replace line 61 (index 60)
lines[60] = "            stmt.executeUpdate(q1 + \"(3, 'What is the correct HTML attribute for referring to an external script?', 'href', 'src', 'link', 'script', 'B')\");\n"

with open(path, 'w', encoding='utf-8') as f:
    f.writelines(lines)

print("Line 61 fixed by line index successfully!")
