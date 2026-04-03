import os

folder = r"c:\Users\prath\.gemini\antigravity\scratch\SmartExamSystem\web\pages"

# Map old blue/purple hexes to new Gold variables
targets = [
    ("#6366f1", "var(--brand-1)"),
    ("#8b5cf6", "var(--accent)"),
    ("#22d3ee", "var(--brand-3)"),
    ("#a5b4fc", "var(--brand-2)"),
    ("linear-gradient(135deg,#6366f1,#8b5cf6)", "linear-gradient(135deg, var(--brand-1), var(--accent))")
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
            print("Fixed colors in " + file)

print("All inline color overrides finalized!")
