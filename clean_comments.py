#!/usr/bin/env python3
"""
Script untuk membersihkan komentar bahasa Inggris dari file Java
Mengganti dengan bahasa Indonesia casual yang penting saja
"""

import re
import os
from pathlib import Path

def clean_java_comments(content):
    """
    Bersihkan komentar yang tidak perlu, ubah yang penting ke Indo casual
    """
    lines = content.split('\n')
    cleaned_lines = []
    in_javadoc = False
    skip_next_javadoc = False
    
    for i, line in enumerate(lines):
        stripped = line.strip()
        
        # Skip javadoc blocks yang tidak penting (getter/setter, constructor sederhana)
        if stripped.startswith('/**'):
            in_javadoc = True
            # Check if it's a simple javadoc (< 3 lines)
            javadoc_end = i
            for j in range(i, min(i+5, len(lines))):
                if '*/' in lines[j]:
                    javadoc_end = j
                    break
            
            javadoc_content = '\n'.join(lines[i:javadoc_end+1])
            
            # Skip javadocs untuk getter/setter/constructor sederhana
            if any(word in javadoc_content.lower() for word in [
                'creates new', 'creates a', 'sets the', 'gets the', 
                'returns the', '@return', '@param', 'constructor'
            ]) and (javadoc_end - i) < 4:
                # Skip this javadoc entirely
                for _ in range(javadoc_end - i + 1):
                    continue
                in_javadoc = False
                continue
        
        if in_javadoc:
            if '*/' in stripped:
                in_javadoc = False
            continue
        
        # Remove simple single-line comments yang ga penting
        if stripped.startswith('//'):
            # Keep komentar penting (ada "penting", "note", "todo", "fixme", "hack", "bug")
            comment_lower = stripped.lower()
            if any(word in comment_lower for word in ['penting', 'note', 'todo', 'fixme', 'hack', 'bug', 'warning', 'bahaya']):
                cleaned_lines.append(line)
            # Keep komentar yang sudah bahasa Indonesia
            elif any(char in stripped for char in ['yang', 'ini', 'untuk', 'di', 'ke', 'dari', 'nya', 'kalo', 'biar', 'jangan']):
                cleaned_lines.append(line)
            # Skip semua komentar bahasa Inggris yang sederhana
            else:
                continue
        else:
            # Keep line biasa
            cleaned_lines.append(line)
    
    return '\n'.join(cleaned_lines)

def process_java_file(file_path):
    """Process single Java file"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        cleaned = clean_java_comments(content)
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(cleaned)
        
        print(f"[OK] Cleaned: {file_path}")
        return True
    except Exception as e:
        print(f"[ERROR] Error processing {file_path}: {e}")
        return False

def main():
    """Main function"""
    java_dir = Path("src/main/java")
    
    if not java_dir.exists():
        print("Error: src/main/java directory not found!")
        return
    
    java_files = list(java_dir.rglob("*.java"))
    total = len(java_files)
    
    print(f"Found {total} Java files to process...")
    print("=" * 60)
    
    success_count = 0
    for java_file in java_files:
        if process_java_file(java_file):
            success_count += 1
    
    print("=" * 60)
    print(f"Completed: {success_count}/{total} files processed successfully")

if __name__ == "__main__":
    main()
