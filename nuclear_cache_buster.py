import os
import shutil

tomcat_css = r"C:\New folder\apache-tomcat-9.0.115\webapps\SmartExamSystem\css"
tomcat_css2 = r"C:\New folder\apache-tomcat-9.0.115\webapps\SmartExamSystem\css2"

if not os.path.exists(tomcat_css2):
    os.makedirs(tomcat_css2)

shutil.copy2(os.path.join(tomcat_css, 'style.css'), os.path.join(tomcat_css2, 'style.css'))
shutil.copy2(os.path.join(tomcat_css, 'dashboard.css'), os.path.join(tomcat_css2, 'dashboard.css'))

print("Created and populated css2 folder!")

src_folder = r"c:\Users\prath\.gemini\antigravity\scratch\SmartExamSystem\web\pages"
tomcat_pages = r"C:\New folder\apache-tomcat-9.0.115\webapps\SmartExamSystem\pages"

def update_links(folder):
    for file in os.listdir(folder):
        if file.endswith(".html"):
            path = os.path.join(folder, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            changed = False
            if '../css/style.css?v=gold' in content:
                content = content.replace('../css/style.css?v=gold', '../css2/style.css')
                changed = True
            elif '../css/style.css' in content:
                content = content.replace('../css/style.css', '../css2/style.css')
                changed = True
                
            if '../css/dashboard.css?v=gold' in content:
                content = content.replace('../css/dashboard.css?v=gold', '../css2/dashboard.css')
                changed = True
            elif '../css/dashboard.css' in content:
                content = content.replace('../css/dashboard.css', '../css2/dashboard.css')
                changed = True
                
            if 'background:linear-gradient(135deg,#6366f1,#8b5cf6)' in content:
                content = content.replace('background:linear-gradient(135deg,#6366f1,#8b5cf6)', 'background:linear-gradient(135deg,var(--brand-1),var(--accent))')
                changed = True
                
            if changed:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print("Updated links in " + file)

update_links(src_folder)
update_links(tomcat_pages)

print("Nuclear cache bust applied!")
