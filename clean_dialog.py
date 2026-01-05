#!/usr/bin/env python3
"""
Remove A./B. prefixes and quotes from dialog choices
"""
import re
from pathlib import Path

def clean_dialog_choices(content):
    """Remove A./B. and quotes from dialog choices"""
    # Pattern: "A. \"text\"" or "B. \"text\""
    pattern = r'"([AB]\.\s*\\")(.*?)(\\")"'
    replacement = r'"\2"'
    
    result = re.sub(pattern, replacement, content)
    return result

def main():
    file_path = Path("src/main/java/controller/CutsceneManager.java")
    
    if not file_path.exists():
        print(f"Error: {file_path} not found!")
        return
    
    content = file_path.read_text(encoding='utf-8')
    cleaned = clean_dialog_choices(content)
    
    file_path.write_text(cleaned, encoding='utf-8')
    print(f"[OK] Cleaned dialog choices in {file_path}")

if __name__ == "__main__":
    main()
