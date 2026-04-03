import os

folder = r"c:\Users\prath\.gemini\antigravity\scratch\SmartExamSystem\web\pages"

targets = [
    ('../css/style.css', '../css/style.css?v=gold'),
    ('../css/dashboard.css', '../css/dashboard.css?v=gold')
]

for file in os.listdir(folder):
    if file.endswith(".html"):
        path = os.path.join(folder, file)
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        changed = False
        for old, new in targets:
            if old in content:
                content = content.replace(old, new)
                changed = True
        
        if changed:
            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)
            print("Busted cache in " + file)

print("All cache busters applied!")
