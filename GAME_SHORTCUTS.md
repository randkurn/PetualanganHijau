# Game Shortcuts & Debug Commands

## 🎮 Player Shortcuts

### F2 - Force Unlock (Emergency Unstuck)
**Fungsi**: Memaksa unlock player jika stuck/tidak bisa bergerak
**Kapan digunakan**: Ketika player terjebak dalam cutscene atau dialog dan tidak bisa bergerak

**Apa yang di-reset**:
- ✓ Game state → PLAY
- ✓ Player movement enabled
- ✓ Player speed → 4 (normal)
- ✓ Cutscene manager direset
- ✓ Active dialogs dihapus
- ✓ Player direction → down

**Cara Pakai**:
```
Tekan F2 kapan saja dalam game
```

**Log Output**:
```
[GamePanel] Force Unlock triggered!
SAFETY: Player movement force-unlocked.
```

## 🔧 Debug Commands

### F3 - Toggle Debug Overlay (jika ada)
**Fungsi**: Menampilkan/menyembunyikan debug info
- FPS counter
- Player coordinates
- Current map
- Game state

### Other Shortcuts
- **ESC** - Pause menu / Cancel
- **SPACE** - Skip dialog / Continue story
- **E** - Interact dengan objects/NPCs
- **I** - Open inventory
- **M** - Open map
- **W/A/S/D atau Arrow Keys** - Movement

## ⚠️ Important Notes

**F2 Shortcut** adalah emergency button:
- Gunakan HANYA jika benar-benar stuck
- Akan mem-bypass semua cutscene/dialog yang sedang berjalan
- Bisa menyebabkan story tidak sesuai jika digunakan di tengah cutscene penting

**Best Practice**:
1. Coba escape (ESC) terlebih dahulu
2. Jika masih stuck, tekan SPACE beberapa kali
3. Sebagai last resort, gunakan F2

## 📝 Untuk Developer

Untuk menambah shortcut baru, edit file:
- `controller/InputManager.java` - Input handling
- `controller/state/PlayState.java` - Play state key handling
- `controller/GamePanel.java` - Game panel methods

Contoh implementasi F2 di PlayState.java:
```java
case KeyEvent.VK_F2 -> {
    gp.forceUnlock();
}
```
