#!/usr/bin/env python3
"""
Cleanup broken javadoc comments - FINAL VERSION
Hapus semua yang mulai dengan /* atau /** atau // (kecuali komentar Indo penting)
"""

import re
from pathlib import Path

def clean_file(content):
    lines = content.split('\n')
    cleaned = []
    in_comment = False
    
    for line in lines:
        stripped = line.strip()
        
        # Skip lines yang cuma comment tags
        if stripped in ['/**', '/*', '*/', '*']:
            in_comment = stripped != '*/'
            continue
        
        # Skip lines yang mulai dengan * (part of javadoc)
        if stripped.startswith('*') and not stripped.startswith('*/'):
            continue
        
        # Handle single line comments
        if '//' in line:
            # Extract comment part
            idx = line.index('//')
            comment = line[idx:]
            code = line[:idx]
            
            # Keep kalo ada keyword Indo
            if any(word in comment.lower() for word in [
                'biar', 'kalo', 'jangan', 'yang', 'untuk', 'ini', 'itu',
                'ga ', 'gak ', 'nge', 'nya', 'pas ', 'dari', 'ke ', 'di ',
                'penting', 'note', 'todo', 'fixme', 'warning', 'bug'
            ]):
                cleaned.append(line)
            elif code.strip():
                cleaned.append(code.rstrip())
        else:
            cleaned.append(line)
    
    # Remove multiple blank lines
    result = '\n'.join(cleaned)
    result = re.sub(r'\n\n\n+', '\n\n', result)
    return result

def main():
    java_dir = Path("src/main/java")
    files = list(java_dir.rglob("*.java"))
    
    print(f"Cleaning {len(files)} files...")
    print("=" * 60)
    
    for f in files:
        try:
            content = f.read_text(encoding='utf-8')
            cleaned = clean_file(content)
            f.write_text(cleaned, encoding='utf-8')
            print(f"[OK] {f}")
        except Exception as e:
            print(f"[ERR] {f}: {e}")
    
    print("=" * 60)
    print("Done!")

if __name__ == "__main__":
    main()
