import os
import shutil

src = r"c:\Users\prath\.gemini\antigravity\scratch\SmartExamSystem\web\pages"
dest = r"C:\New folder\apache-tomcat-9.0.115\webapps\SmartExamSystem\pages"

# Create destination if missing
if not os.path.exists(dest):
    os.makedirs(dest)

copied = 0
for file in os.listdir(src):
    if file.endswith(".html"):
        src_path = os.path.join(src, file)
        dest_path = os.path.join(dest, file)
        shutil.copy2(src_path, dest_path)
        print("Deployed " + file)
        copied += 1

print("All " + str(copied) + " HTML pages deployed securely!")
