#!/usr/bin/env python3
"""
Script untuk membersihkan komentar dari file Java
v2 - Lebih aman, menghapus block javadoc lengkap
"""

import re
from pathlib import Path

def clean_java_file(content):
    """
    Hapus javadoc blocks dan comment blocks yang ga penting
    """
    # Atur regex patterns
    javadoc_pattern = r'/\*\*.*?\*/'
    multiline_comment_pattern = r'/\*.*?\*/'
    single_comment_pattern = r'//.*?$'
    
    # Remove javadoc comments (yang ada /** ... */)
    content = re.sub(javadoc_pattern, '', content, flags=re.DOTALL)
    
    # Remove multi-line comments (yang ada /* ... */)  
    content = re.sub(multiline_comment_pattern, '', content, flags=re.DOTALL)
    
    # Process line by line untuk single-line comments
    lines = content.split('\n')
    cleaned_lines = []
    
    for line in lines:
        # Keep komentar Indo yang penting (ada keyword spesifik)
        if '//' in line:
            comment_part = line[line.index('//'):]
            # Keep kalo ada bahasa Indonesia keywords
            if any(word in comment_part.lower() for word in [
                'biar', 'kalo', 'jangan', 'yang', 'untuk', 'ini', 'itu',
                'ga ', 'gak ', 'nge', 'nya', 'pas ', 'dari', 'ke ', 'di ',
                'penting', 'note', 'todo', 'fixme', 'hack', 'bug'
            ]):
                cleaned_lines.append(line)
            else:
                # Hapus komentar bahasa Inggris
                code_part = line[:line.index('//')]
                if code_part.strip():  # Only keep if there's code before comment
                    cleaned_lines.append(code_part.rstrip())
        else:
            cleaned_lines.append(line)
    
    result = '\n'.join(cleaned_lines)
    
    # Clean up multiple consecutive blank lines jadi cuma 2
    result = re.sub(r'\n\n\n+', '\n\n', result)
    
    return result

def main():
    java_dir = Path("src/main/java")
    
    if not java_dir.exists():
        print("Error: src/main/java not found!")
        return
    
    java_files = list(java_dir.rglob("*.java"))
    print(f"Processing {len(java_files)} Java files...")
    print("=" * 60)
    
    success = 0
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            cleaned = clean_java_file(content)
            
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(cleaned)
            
            print(f"[OK] {file_path}")
            success += 1
        except Exception as e:
            print(f"[ERROR] {file_path}: {e}")
    
    print("=" * 60)
    print(f"Done: {success}/{len(java_files)} files")

if __name__ == "__main__":
    main()
