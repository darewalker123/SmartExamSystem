import os

start_dir = r"C:\New folder\apache-tomcat-9.0.115"

for root, dirs, files in os.walk(start_dir):
    for file in files:
        if "mysql" in file.lower() and file.endswith(".jar"):
            print("FOUND:", os.path.join(root, file))
