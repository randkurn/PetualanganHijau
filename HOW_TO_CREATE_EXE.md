# 🎮 Petualangan Hijau - Panduan Membuat EXE

## ✅ OPSI 1: BAT Launcher (Paling Simple - SUDAH TERSEDIA)

File `build/exe/PetualanganHijau.bat` sudah dibuat! Cukup:

1. Copy file `PetualanganHijau_New.jar` ke folder `build/exe/`
2. Double-click `PetualanganHijau.bat` untuk menjalankan game

**Kelebihan:**
- ✅ No installation needed
- ✅ Works immediately
- ✅ Small file size

**Kekurangan:**
- ❌ Still shows command prompt window
- ❌ Users need Java installed

---

## 🔧 OPSI 2: Convert ke EXE Manual (Recommended)

### Method A: Menggunakan Launch4j (Recommended)

1. **Download Launch4j**
   - https://sourceforge.net/projects/launch4j/
   - Extract ke `C:\Program Files\Launch4j` atau folder lainnya

2. **Jalankan Launch4j GUI**
   - Buka `launch4j.exe`

3. **Konfigurasi:**
   - **Output file:** `C:\path\to\build\exe\PetualanganHijau.exe`
   - **Jar:** `C:\path\to\build\PetualangHijau_New.jar`
   - **Icon:** (Optional) Pilih icon .ico jika ada
   
4. **Tab "JRE":**
   - **Min JRE version:** `21`
   - **Initial heap size:** `256`
   - **Max heap size:** `1024`
   
5. **Tab "Version Info":**
   - **File version:** `1.0.0.0`
   - **Product name:** `Petualangan Hijau`
   - **Company:** `CMPT276 Group 7`
   - **File description:** `Game Edukasi Lingkungan`

6. **Klik "Build Wrapper"**

### Method B: Menggunakan jpackage (Built-in Java)

**REQUIREMENTS:**
- WiX Toolset 3.0+ (Download: https://wixtoolset.org)
- Add WiX to PATH

**Command:**
```powershell
jpackage --type exe --name PetualanganHijau --app-version 1.0.0 --input build --main-jar PetualangHijau_New.jar --main-class controller.App --dest build/exe --vendor "CMPT276 Group 7" --win-console --win-shortcut --win-menu
```

### Method C: Menggunakan exe4j (Commercial)

1. Download: https://www.ej-technologies.com/products/exe4j/overview.html
2. Free trial available
3. Follow GUI wizard

---

## 📦 OPSI 3: Portable Package (User-Friendly)

Buat folder distribusi:

```
PetualanganHijau/
├── PetualanganHijau.jar     (renamed dari PetualangHijau_New.jar)
├── START.bat                 (launcher)
├── README.txt                (instructions)
└── jre/                      (Optional: bundled Java runtime)
```

**START.bat:**
```batch
@echo off
start javaw -jar PetualanganHijau.jar
```

**README.txt:**
```
PETUALANGAN HIJAU
=================

Cara Main:
1. Install Java 21: https://adoptium.net/
2. Double-click START.bat

Atau langsung:
java -jar PetualanganHijau.jar
```

---

## 🚀 OPSI 4: Installer (Advanced)

### Menggunakan Inno Setup

1. Download: https://jrsoftware.org/isinfo.php

2. Buat script `installer.iss`:

```inno
[Setup]
AppName=Petualangan Hijau
AppVersion=1.0.0
DefaultDirName={pf}\PetualanganHijau
DefaultGroupName=Petualangan Hijau
OutputDir=build\installer
OutputBaseFilename=PetualanganHijau_Setup

[Files]
Source: "build\PetualangHijau_New.jar"; DestDir: "{app}"; DestName: "PetualanganHijau.jar"
Source: "Artifacts\*"; DestDir: "{app}\lib"; Flags: recursesubdirs

[Icons]
Name: "{group}\Petualangan Hijau"; Filename: "javaw.exe"; Parameters: "-jar ""{app}\PetualanganHijau.jar"""; WorkingDir: "{app}"
Name: "{commondesktop}\Petualangan Hijau"; Filename: "javaw.exe"; Parameters: "-jar ""{app}\PetualanganHijau.jar"""; WorkingDir: "{app}"

[Run]
Filename: "javaw.exe"; Parameters: "-jar ""{app}\PetualanganHijau.jar"""; Description: "Launch Petualangan Hijau"; Flags: postinstall nowait skipifsilent
```

3. Compile dengan Inno Setup Compiler

---

## 📋 Rekomendasi Per Use Case

| Use Case | Metode | Effort |
|----------|--------|--------|
| **Testing/Development** | BAT file | ⚡ Instant |
| **Personal distribution** | Launch4j EXE | ⭐ Easy |
| **Professional release** | jpackage installer | 🔧 Medium |
| **No Java required** | Bundle JRE + EXE | 🎯 Advanced |

---

## ❓ FAQ

**Q: User harus install Java?**
A: Ya, kecuali kalau bundle JRE (file jadi ~100MB+)

**Q: EXE kok besar?**
A: JAR sudah ~50MB karena ada assets. Normal untuk game Java.

**Q: BAT vs EXE, mana lebih baik?**
A: EXE lebih professional, tapi BAT lebih simple dan portabel.

**Q: Bisa convert ke .app (macOS) atau AppImage (Linux)?**
A: Ya! jpackage support semua platform:
- macOS: `--type dmg` atau `--type pkg`
- Linux: `--type deb` atau `--type rpm`

---

## 🎯 Quick Start (Pake BAT)

```powershell
# 1. Copy JAR
Copy-Item "build\PetualangHijau_New.jar" "build\exe\"

# 2. Run
.\build\exe\PetualanganHijau.bat
```

Done! 🎮
