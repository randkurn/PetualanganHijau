# 🌳 Petualangan Hijau

<div align="center">

![Petualangan Hijau Banner](Documents/banner.png)

**A 2D Environmental Adventure Game**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](build/)

*Belajar Jaga Lingkungan Lewat Petualangan Seru!*

[📥 Download](#download) • [🎮 Cara Main](#cara-bermain) • [🛠️ Development](#development) • [📜 Credits](#credits)

</div>

---

## 📖 Tentang Game

**Petualangan Hijau** adalah game edukasi 2D berbasis Java yang mengajarkan pentingnya menjaga lingkungan melalui gameplay yang interaktif dan menyenangkan. Ikuti perjalanan seorang anak muda yang belajar pentingnya kebersihan lingkungan, pemilahan sampah, dan penanaman pohon untuk mencegah banjir.

### 🎯 Fitur Utama

- **🗑️ Sistem Pemilahan Sampah 3 Kategori**
  - Organik, Anorganik, dan B3 (Bahan Berbahaya & Beracun)
  - Sistem scoring berdasarkan akurasi pemilahan
  - Bonus gold untuk pemilahan yang benar

- **🌱 Sistem Penanaman Pohon**
  - Tanam bibit mahoni dan pohon lainnya
  - Fakta edukatif tentang pohon Indonesia
  - Loading screen interaktif dengan informasi lingkungan

- **📊 Environment Meter**
  - Tracking real-time kebersihan lingkungan
  - Dampak langsung dari aksi player
  - Visual feedback untuk kondisi lingkungan

- **🎭 Cerita Interaktif 2+ Chapter**
  - Chapter 1: Langkah Kecil (Pengenalan)
  - Chapter 2: Jaringan Kebaikan (Komunitas)
  - Chapter 3-5: Coming Soon!

- **🗺️ Multiple Maps**
  - Rumah, Halaman, Bank Sampah, Lapak Bibit
  - TMX format support (Tiled Map Editor)
  - Dynamic object spawning

- **💰 Ekonomi In-Game**
  - Tukar sampah dapat gold
  - Beli bibit pohon
  - Sistem inventory management

- **🎵 Audio System**
  - Background music
  - Sound effects
  - Adjustable volume controls

---

## 🎮 Cara Bermain

### Kontrol

| Tombol | Fungsi |
|--------|--------|
| **W/A/S/D** | Gerak (Atas/Kiri/Bawah/Kanan) |
| **E** | Interaksi dengan NPC/Object |
| **P** | Plant Tree (di area farm) |
| **I** | Buka Inventory |
| **M** | Buka Minimap (Fullscreen) |
| **T** | Teleport Menu (unlocked areas) |
| **ESC** | Pause Menu |
| **SPACE** | Skip Dialog / Next |
| **ENTER** | Select / Confirm |

### Gameplay Tips

1. **Kumpulkan Sampah** - Otomatis terpungut saat dekat
2. **Pilah Dengan Benar** - Perhatikan kategori sampah (Organik/Anorganik/B3)
3. **Tukar di Bank Sampah** - Dapatkan gold untuk bibit
4. **Tanam Pohon** - Di area yang sudah ditentukan saja
5. **Jaga Energi** - Tidur untuk restore energy
6. **Follow NPC Hints** - Danu, Bu Suci, dan Pak Saman akan membimbing

---

## 📥 Download

### Requirement
- **Java Runtime Environment (JRE) 21** atau lebih baru
- **OS:** Windows, macOS, atau Linux
- **RAM:** Minimum 512 MB
- **Storage:** ~50 MB

### Installation

1. **Download JRE 21** (jika belum ada)
   - [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
   - [OpenJDK](https://adoptium.net/)

2. **Download Game**
   ```
   Artifacts/UntitledFarmGame.jar  (Latest Build)
   ```

3. **Run Game**
   ```bash
   # Windows
   java -jar UntitledFarmGame.jar

   # macOS/Linux
   java -jar UntitledFarmGame.jar
   ```

### Build dari Source

```bash
# Clone repository
git clone [repository-url]
cd Untitled-Farm-Game-master

# Compile (Windows PowerShell)
.\build.ps1

# Compile (Manual with Maven)
mvn clean package

# Run
java -jar build/PetualangHijau_New.jar
```

---

## 🛠️ Development

### Tech Stack

- **Language:** Java 21
- **Build Tool:** Maven 3.x
- **IDE:** NetBeans / IntelliJ IDEA / Eclipse
- **Map Editor:** Tiled Map Editor (TMX format)
- **Graphics:** Java 2D Graphics API
- **Audio:** Java Clip API

### Project Structure

```
Untitled-Farm-Game-master/
├── src/
│   └── main/
│       ├── java/
│       │   ├── controller/      # Game logic, managers
│       │   ├── model/           # Entities, data models
│       │   └── view/            # UI screens, rendering
│       └── resources/           # Assets
│           ├── fonts/           # Press Start 2P
│           ├── maps/            # TMX map files
│           ├── music/           # Background music
│           ├── sound/           # Sound effects
│           ├── tiles/           # Tile images
│           ├── boy/             # Player sprites
│           ├── NPC/             # NPC sprites
│           ├── objects/         # Game objects
│           └── story/           # Story text files
├── Artifacts/                   # Build outputs
├── Documents/                   # Documentation
├── build.ps1                    # Build script
└── pom.xml                      # Maven config
```

### Architecture

- **MVC Pattern**
  - `model/` - Game entities, inventory, data
  - `view/` - UI screens, rendering
  - `controller/` - Game loop, state management

- **Core Systems**
  - `GamePanel` - Main game loop (60 FPS)
  - `StateManager` - Game state transitions
  - `NPCManager` - NPC spawning & behavior
  - `MapManager` - Map loading & transitions
  - `EnvironmentManager` - Environment scoring
  - `SaveManager` - Save/Load system

### Adding New Content

#### Add New Map
1. Create map in **Tiled Map Editor** (TMX)
2. Export to `src/main/resources/maps/[name].tmx`
3. Add to `MapManager.java` maps array
4. Define spawn points and objects

#### Add New NPC
1. Create class extending `Entity`
2. Implement `interact()` method
3. Load sprites from `resources/NPC/[name]/`
4. Register in `NPCManager.java`

#### Add New Item/Trash
1. Define in `TrashRegistry.java`
2. Add sprite to `resources/objects/`
3. Set category (ORGANIC/ANORGANIC/TOXIC)
4. Add to `Inventory.java` icon mappings

---

## 🐛 Known Issues

- [ ] Pathfinding can get stuck on diagonal obstacles
- [ ] Minimap doesn't show all NPCs in large maps
- [ ] Sorting UI can overlap in some fullscreen resolutions
- [ ] Trash respawn after save/load (fix in progress)

---

## 🚀 Roadmap

### Version 1.1 (In Progress)
- [x] Chapter 1 & 2 Complete
- [x] Tree planting with educational facts
- [ ] **Chapter 3: Neraca Alam** (Environment Balance)
- [ ] **Chapter 4: Peringatan Badai** (Storm Warning)
- [ ] **Chapter 5: Hari Penentuan** (Judgment Day - Endings)

### Version 1.2 (Planned)
- [ ] Achievements system
- [ ] Multiple language support (EN/ID)
- [ ] More tree varieties
- [ ] Additional NPCs
- [ ] Mini-games (composting, recycling)

### Version 2.0 (Future)
- [ ] Multiplayer co-op mode
- [ ] Leaderboard system
- [ ] Custom map creator
- [ ] Mobile port (Android/iOS)

---

## 📜 Credits

### 👨‍💻 Development Team

**Petualangan Hijau** was developed by the **CMPT276 F22 Group 7** team at Simon Fraser University.

#### Core Contributors
- **Andrew Hein (ach17)** - Lead Developer, Core Systems
- **Jeffrey Jin (jjj9)** - UI/UX, Game States, Audio
- **Long Nguyen (dln3)** - Input System, Controls
- **Navanza Varel AM (2409181)** - Teleport System, Maps
- **Rand** - Additional Features, Indonesian Localization

### 🎨 Assets & Resources

#### Graphics
- **Pixel Art Tiles & Sprites** - Custom created and curated
- **Background Images** - Various pixel art sources
- **UI Icons** - Press Start 2P font elements

#### Fonts
- **Press Start 2P** by CodeMan38
  - License: SIL Open Font License 1.1
  - [Google Fonts](https://fonts.google.com/specimen/Press+Start+2P)

#### Audio
- Background Music: Royalty-free game music
- Sound Effects: Compiled from free SFX libraries

#### Map Editor
- **Tiled Map Editor** by Thorbjørn Lindeijer
  - [Website](https://www.mapeditor.org)
  - License: GPL 2.0

### 📚 Educational References

- **RyiSnow** - Java 2D Game Development Tutorial
  - [YouTube Channel](https://www.youtube.com/@RyiSnow)
  - Excellent tutorial series on Java 2D game programming
  - Inspiration for game architecture and core mechanics

### 🌍 Environmental Data Sources

Tree and environmental facts were compiled from:
- Indonesian Ministry of Environment and Forestry
- World Wildlife Fund (WWF) Indonesia
- Various environmental NGOs and research papers

---

## 📄 License

This project is licensed under the **MIT License**.

```
MIT License

Copyright (c) 2022-2026 CMPT276 F22 Group 7

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🌟 Special Thanks

- **Our Players** - For supporting environmental education through gaming
- **SFU CMPT276** - For the opportunity to create this project
- **Indonesian Environmental Community** - For inspiration and data
- **RyiSnow** - For the excellent Java 2D game tutorial on YouTube
- **Open Source Community** - For tools and libraries

---

## 📞 Contact & Support

- **Issues:** [GitHub Issues](../../issues)
- **Discussions:** [GitHub Discussions](../../discussions)
- **Email:** [Contact Team]

---

## 🌱 Contribute

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### How to Contribute
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

<div align="center">

**Dibuat dengan ❤️ untuk Bumi yang Lebih Hijau**

*"Kita jaga alam, alam jaga kita"*

⭐ **Star this repo if you support environmental education!** ⭐

</div>
