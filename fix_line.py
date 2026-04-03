path = r"c:\Users\prath\.gemini\antigravity\scratch\SmartExamSystem\src\database\LoadTestData.java"

with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix the broken line 61 accurately
bad_str = 'stmt.executeUpdate(q1 + "(3, \'What is correct syntax for referring an external script in HTML?\', \'<script href=\\\"...\\\">\', \'<script src=\\\"...\\\">\', \'<script link=\\\"...\\\">\', \'<link src=\\\"...\\\">\', \'B\')\");'

good_str = 'stmt.executeUpdate(q1 + "(3, \'What is the correct HTML attribute for referring to an external script?\', \'href\', \'src\', \'link\', \'script\', \'B\')\");'

content = content.replace(bad_str, good_str)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Line 61 fixed successfully!")
