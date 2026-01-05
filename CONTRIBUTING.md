# Contributing to Petualangan Hijau

Terima kasih atas minat Anda untuk berkontribusi pada **Petualangan Hijau**! 🌳

## 🎯 Cara Berkontribusi

### 1. Melaporkan Bug
- Gunakan [GitHub Issues](../../issues)
- Jelaskan langkah-langkah untuk mereproduksi bug
- Sertakan screenshot jika memungkinkan
- Sebutkan versi Java dan OS yang digunakan

### 2. Mengusulkan Fitur Baru
- Buka [GitHub Discussions](../../discussions)
- Jelaskan fitur yang diinginkan
- Berikan alasan mengapa fitur ini penting
- Diskusikan implementasi yang mungkin

### 3. Submit Pull Request

#### Proses
1. Fork repository
2. Buat branch baru dari `main`
   ```bash
   git checkout -b feature/nama-fitur
   ```
3. Lakukan perubahan
4. Test perubahan Anda
5. Commit dengan pesan yang jelas
   ```bash
   git commit -m "Add: Deskripsi fitur baru"
   ```
6. Push ke branch Anda
   ```bash
   git push origin feature/nama-fitur
   ```
7. Buat Pull Request

#### Commit Message Convention
- `Add:` - Fitur baru
- `Fix:` - Perbaikan bug
- `Update:` - Update fitur existing
- `Refactor:` - Code refactoring
- `Docs:` - Update dokumentasi

### 4. Coding Standards

#### Java Style
- Gunakan **camelCase** untuk variables dan methods
- Gunakan **PascalCase** untuk class names
- Indentasi: 4 spaces (no tabs)
- Line length: Max 120 characters
- Tambahkan comments untuk logic yang kompleks

#### File Organization
```
controller/  - Game logic, managers
model/       - Data structures, entities
view/        - UI, rendering
```

#### Naming Conventions
```java
// Class
public class PlayerInventory { }

// Method
public void addItem(String name, int quantity) { }

// Variable
private int currentHealth;
private final int MAX_HEALTH = 100;
```

### 5. Testing
- Test game di berbagai resolusi
- Test semua fitur yang dimodifikasi
- Pastikan tidak ada error saat compile
- Cek console untuk warning

## 📋 Areas Yang Membutuhkan Kontribusi

### High Priority
- [ ] Implementasi Chapter 3-5
- [ ] Fix trash respawn bug setelah save/load
- [ ] Improve pathfinding algorithm
- [ ] Add more educational content

### Medium Priority
- [ ] Achievement system
- [ ] Bahasa Inggris support (full translation)
- [ ] More NPC interactions
- [ ] Additional maps

### Low Priority
- [ ] Sound effect variety
- [ ] Particle effects
- [ ] Day/night cycle
- [ ] Weather system

## 🎨 Asset Contribution

### Sprites
- Format: PNG with transparency
- Size: 16x16 or multiples (32x32, 48x48)
- Style: 8-bit/16-bit pixel art

### Music/SFX
- Format: WAV or MP3
- Royalty-free or original
- Appropriate for educational game

### Maps
- Tool: Tiled Map Editor
- Format: TMX
- Tile size: 16x16

## 🔍 Code Review Process

1. Pull request akan di-review oleh maintainer
2. Feedback akan diberikan dalam 3-7 hari
3. Revisi jika diperlukan
4. Merge setelah approval

## 📖 Resources

- [Java 2D Graphics Tutorial](https://docs.oracle.com/javase/tutorial/2d/)
- [RyiSnow Java Game Tutorial](https://www.youtube.com/@RyiSnow)
- [Tiled Map Editor Docs](https://doc.mapeditor.org/)

## 💬 Komunikasi

- **Pertanyaan Umum:** GitHub Discussions
- **Bug Reports:** GitHub Issues
- **Feature Requests:** GitHub Discussions

## 🌟 Recognition

Semua contributor akan disebutkan di:
- README.md Credits section
- In-game Credits screen
- Release notes

---

**Terima kasih telah membantu membuat Petualangan Hijau lebih baik!** 🎮🌱
