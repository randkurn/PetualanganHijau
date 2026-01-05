# Panduan Membuat EXE untuk Petualangan Hijau

## Prasyarat
- Java JDK 21 sudah terinstall ✓
- JAR file sudah ada di `dist/PetualangHijau_New.jar` ✓
- Launch4j (akan didownload)

## Langkah-langkah:

### 1. Download Launch4j
1. Buka browser dan kunjungi: https://launch4j.sourceforge.net/
2. Download versi terbaru (Launch4j 3.x)
3. Extract ke folder `C:\Program Files\Launch4j` atau folder lain yang Anda inginkan

### 2. Buat EXE Menggunakan Launch4j GUI

#### Cara Manual (Recommended):
1. Buka Launch4j (launch4j.exe)
2. Isi konfigurasi berikut:

**Tab "Basic":**
- Output file: `C:\Users\Rand\Documents\NetBeansProjects\Untitled-Farm-Game-master\build\PetualanganHijau.exe`
- Jar: `C:\Users\Rand\Documents\NetBeansProjects\Untitled-Farm-Game-master\dist\PetualangHijau_New.jar`
- Don't wrap jar: ☐ (unchecked)
- Icon: (optional - bisa dikosongkan atau pilih icon .ico)

**Tab "JRE":**
- Min JRE version: `21`
- Initial heap size (MB): `512`
- Max heap size (MB): `2048`

**Tab "Version Info":**
- File version: `1.0.0.0`
- Product version: `1.0.0.0`
- File description: `Petualangan Hijau - Environmental Adventure Game`
- Product name: `Petualangan Hijau`
- Company name: `Kelompok 3 Aselole`
- Copyright: `2026 Kelompok 3 Aselole`

3. Klik menu **Build wrapper** atau tekan **Ctrl+B**
4. EXE akan dibuat di folder `build/`

#### Cara Command Line (Alternatif):
```powershell
# Setelah Launch4j terinstall, jalankan:
cd "C:\Users\Rand\Documents\NetBeansProjects\Untitled-Farm-Game-master"
"C:\Program Files\Launch4j\launch4jc.exe" launch4j-config.xml
```

### 3. Distribusi

Setelah EXE dibuat, Anda perlu mendistribusikan 2 file:
```
build/
├── PetualanganHijau.exe    (launcher - ~1 MB)
└── PetualangHijau_New.jar  (game files - ~50 MB)
```

**PENTING:** Kedua file harus berada di folder yang sama!

### 4. Testing

Jalankan `PetualanganHijau.exe` untuk memastikan game berjalan dengan baik.

## Troubleshooting

### Error: "Java not found"
- Install Java JRE 21 dari: https://www.oracle.com/java/technologies/downloads/

### Error: "JAR not found"
- Pastikan `PetualangHijau_New.jar` ada di folder yang sama dengan EXE

### Game tidak muncul
- Buka Command Prompt dan jalankan:
  ```
  java -jar PetualangHijau_New.jar
  ```
  Untuk melihat error message

## File Konfigurasi

File `launch4j-config.xml` sudah disiapkan di root project.
Anda bisa langsung load file ini di Launch4j GUI:
1. Buka Launch4j
2. File → Load Config
3. Pilih `launch4j-config.xml`
4. Build wrapper

## Catatan untuk GitHub

Sebelum push ke GitHub, pastikan:
- [ ] File `.jar` ada di `dist/`
- [ ] File `.exe` ada di `build/` (optional, bisa di-gitignore)
- [ ] README.md sudah update
- [ ] File konfigurasi Launch4j tersimpan untuk reproduksi

## Membuat Release di GitHub

```bash
# 1. Commit semua perubahan
git add .
git commit -m "Release v1.0 - Petualangan Hijau"

# 2. Tag release
git tag -a v1.0 -m "Version 1.0 - Full Chapter 1 & 2"

# 3. Push dengan tags
git push origin main --tags

# 4. Di GitHub, buat Release baru dan upload:
#    - PetualanganHijau.exe
#    - PetualangHijau_New.jar
#    - README.md dengan instruksi instalasi
```

## Alternatif: Membuat Installer

Untuk membuat installer yang lebih profesional, gunakan:
- **Inno Setup** (Windows): https://jrsoftware.org/isinfo.php
- **jpackage** (Java 21 built-in):
  ```
  jpackage --input dist --name "Petualangan Hijau" --main-jar PetualangHijau_New.jar --type exe
  ```
