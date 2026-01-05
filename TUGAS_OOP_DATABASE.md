# PROYEK AKHIR: PETUALANGAN HIJAU
## Aplikasi Desktop Game Edukasi Lingkungan Berbasis Java dengan Integrasi Database

---

## 📋 TUJUAN PROYEK

### Tujuan Utama
• Membuat aplikasi desktop game edukasi lingkungan yang dapat membantu meningkatkan kesadaran masyarakat tentang pengelolaan sampah dan pelestarian lingkungan di Indonesia.

• Mengimplementasikan konsep Object-Oriented Programming (OOP) seperti inheritance, polymorphism, encapsulation, dan abstraction dalam pengembangan game engine dan game logic.

• Mendesain dan mengelola basis data relasional yang terintegrasi dengan aplikasi untuk menyimpan data player, progress game, achievement, dan collected items.

• Membangun sistem manajemen state yang robust menggunakan State Pattern dan Observer Pattern untuk menangani berbagai kondisi game (Title, Play, Pause, Inventory, etc.).

• Mengimplementasikan sistem save/load yang persistent menggunakan database atau file-based storage dengan format JSON.

### Tujuan Spesifik

**Aspek Pemrograman:**
• Menerapkan prinsip Single Responsibility Principle (SRP) dengan memisahkan concern antara Model, View, dan Controller (MVC Pattern).

• Menggunakan inheritance untuk membuat hierarchy class Entity yang dapat diwariskan ke Player, NPC, dan GameObject.

• Menerapkan polymorphism dalam interaksi berbagai tipe GameObject (Trash, Portal, Bed, Bin) dengan behavior yang berbeda.

• Mengimplementasikan encapsulation untuk melindungi data sensitif seperti player stats, inventory, dan game progress.

**Aspek Database:**
• Merancang Entity-Relationship Diagram (ERD) untuk sistem game dengan minimal 5 entitas (Player, SaveData, Achievement, Inventory, TrashCollection).

• Menormalisasi database hingga bentuk normal ketiga (3NF) untuk menghindari redundansi data.

• Mengimplementasikan CRUD operations (Create, Read, Update, Delete) untuk semua entitas dalam database.

• Membuat query optimization untuk performa yang baik saat load/save game data.

**Aspek User Interface:**
• Membangun GUI menggunakan Java Swing/AWT dengan custom rendering untuk game graphics.

• Mengimplementasikan responsive UI yang dapat beradaptasi dengan fullscreen dan windowed mode.

• Mendesain user experience yang intuitif dengan feedback visual dan audio untuk setiap interaksi.

---

## 🎯 100 KEMUNGKINAN PERTANYAAN UJIAN/PRESENTASI

### BAGIAN 1: KONSEP OOP DASAR (Pertanyaan 1-20)

#### Encapsulation

1. **Jelaskan bagaimana encapsulation diterapkan dalam class `Player`? Mengapa field seperti `energy`, `gold`, dan `score` dibuat private?**
   - *Jawaban*: Field-field tersebut private untuk mencegah akses langsung dari luar class. Akses dilakukan melalui getter/setter methods untuk validasi.

2. **Apa keuntungan menggunakan getter/setter dibandingkan public field di class `Inventory`?**

3. **Bagaimana encapsulation membantu dalam sistem inventory? Berikan contoh method yang melakukan validasi sebelum mengubah data.**

4. **Jelaskan perbedaan access modifier (private, protected, public) yang digunakan dalam project ini dan berikan contoh penggunaannya.**

5. **Mengapa method `health` atau `energy` di Player class dibuat dengan validation? Apa yang terjadi tanpa validation?**

#### Inheritance

6. **Gambarkan dan jelaskan hierarchy class Entity dalam game ini. Class mana saja yang inherit dari Entity?**
   - *Jawaban*: Entity (parent) → Player, Mother, PakBahlil, Panjul, TehDila (children). Entity berisi common properties seperti worldX, worldY, speed, direction, sprites.

7. **Apa keuntungan menggunakan inheritance untuk NPC (Mother, PakBahlil, Panjul, TehDila) dibanding membuat class terpisah tanpa parent?**

8. **Method apa saja yang di-override dari class Entity di class Player? Mengapa perlu di-override?**

9. **Jelaskan konsep method overriding dalam context `update()` method yang ada di Player vs NPC classes.**

10. **Bagaimana inheritance memudahkan penambahan NPC baru dalam game? Berikan contoh step-by-step.**

#### Polymorphism

11. **Jelaskan polymorphism yang terjadi pada method `interact()` di berbagai GameObject (Trash, Portal, Bed, Bin).**
    - *Jawaban*: Setiap GameObject type memiliki implementasi interact() yang berbeda tapi dipanggil dengan cara yang sama, demonstrating runtime polymorphism.

12. **Bagaimana polymorphism diterapkan dalam `StateManager` dengan berbagai GameState (PLAY, PAUSE, INVENTORY, TITLE)?**

13. **Jelaskan perbedaan compile-time polymorphism (method overloading) dan runtime polymorphism (method overriding). Berikan contoh dari code.**

14. **Pada `MapManager.changeArea()`, ada beberapa overloaded methods. Jelaskan mengapa ini contoh polymorphism.**

15. **Bagaimana interface atau abstract class bisa digunakan untuk improve polymorphism dalam game ini?**

#### Abstraction

16. **Apa itu abstraction dan bagaimana UI class sebagai parent dari berbagai screen (TitleScreen, PlayScreen, etc) menerapkan abstraction?**

17. **Jelaskan bagaimana `StateManager` mengabstraksi kompleksitas switching antara berbagai game states.**

18. **Mengapa method `draw()` perlu diabstraksi di semua UI components? Apa keuntungannya?**

19. **Bagaimana abstraction membantu dalam memisahkan game logic dari rendering logic?**

20. **Jelaskan perbedaan antara abstract class dan interface. Mana yang lebih cocok untuk use case tertentu dalam game ini?**

---

### BAGIAN 2: OOP LANJUTAN & DESIGN PATTERNS (Pertanyaan 21-40)

#### Design Patterns

21. **Jelaskan Singleton Pattern yang digunakan di `SaveManager`, `TimeManager`, dan `AchievementManager`. Mengapa pattern ini dipilih?**
    - *Jawaban*: Singleton memastikan hanya ada satu instance manager di seluruh aplikasi, menghindari konflik data dan shared state.

22. **Apa kelebihan dan kekurangan Singleton Pattern? Dalam kondisi apa Singleton bisa menjadi anti-pattern?**

23. **Jelaskan MVC (Model-View-Controller) pattern dalam struktur project ini. Mana yang termasuk Model, View, dan Controller?**

24. **Bagaimana State Pattern diterapkan dalam `StateManager`? Jelaskan state transition diagram.**

25. **Observer Pattern bisa digunakan untuk event system. Bagaimana achievement system bisa di-improve dengan Observer Pattern?**

#### Class Relationships

26. **Jelaskan relationship antara `GamePanel`, `Player`, `MapManager`, dan `NPCManager`. Apakah ini composition atau aggregation?**

27. **Apa perbedaan composition (has-a) dan inheritance (is-a)? Berikan contoh masing-masing dari code.**

28. **Mengapa `Player` memiliki reference ke `GamePanel`? Apa risiko circular dependency?**

29. **Jelaskan loose coupling vs tight coupling. Bagaimana dependency injection bisa improve code quality?**

30. **Bagaimana interface segregation principle bisa diterapkan untuk improve class design dalam game ini?**

#### Error Handling

31. **Bagaimana error handling dilakukan saat loading sprites? Apa fallback mechanism yang diimplementasikan?**

32. **Jelaskan try-catch blocks dalam `SaveManager.loadGame()`. Apa yang terjadi jika file corrupted?**

33. **Mengapa validation penting dalam `Inventory.addItem()`? Apa yang terjadi tanpa validation?**

34. **Bagaimana null pointer exception dicegah dalam interaction dengan GameObject?**

35. **Jelaskan best practice error logging yang diterapkan dalam game ini.**

#### Memory Management

36. **Bagaimana garbage collection bekerja di Java? Objek mana yang eligible untuk garbage collection?**

37. **Mengapa `MapManager.clearAll()` penting? Apa yang terjadi tanpa proper cleanup?**

38. **Jelaskan memory leak potential dalam game loop. Bagaimana cara menghindarinya?**

39. **Bagaimana sprite caching di `TileManager` membantu memory efficiency?**

40. **Apa perbedaan stack memory dan heap memory? Di mana Player object disimpan?**

---

### BAGIAN 3: DATABASE FUNDAMENTALS (Pertanyaan 41-60)

#### Database Design

41. **Gambarkan Entity-Relationship Diagram (ERD) untuk sistem save game. Entitas apa saja yang diperlukan?**
    - *Jawaban*: Entitas: Player, SaveData, TimeData, Inventory, Achievement, TrashCollection, MapState.

42. **Jelaskan normalisasi database. Mengapa perlu normalisasi hingga 3NF?**

43. **Apa yang dimaksud dengan primary key dan foreign key? Berikan contoh dalam context game ini.**

44. **Jelaskan relationship one-to-one, one-to-many, dan many-to-many. Contoh masing-masing dalam game database.**

45. **Bagaimana mendesain table `Inventory` untuk menyimpan items dengan quantities yang berbeda?**

#### SQL Queries

46. **Tulis SQL query untuk create table `Player` dengan columns yang sesuai.**
    ```sql
    CREATE TABLE Player (
        player_id INT PRIMARY KEY AUTO_INCREMENT,
        player_name VARCHAR(50) NOT NULL,
        energy INT DEFAULT 100,
        gold INT DEFAULT 500,
        score INT DEFAULT 0,
        world_x INT,
        world_y INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    ```

47. **Tulis SQL query untuk insert new save data ke database.**

48. **Bagaimana cara melakukan SELECT dengan JOIN antara table Player dan Achievement?**

49. **Tulis query untuk mencari semua achievement yang belum di-unlock oleh player tertentu.**

50. **Bagaimana cara update player stats (energy, gold, score) dalam satu transaction?**

#### CRUD Operations

51. **Jelaskan implementasi CREATE operation dalam `SaveManager.saveGame()`. Apa saja data yang disimpan?**

52. **Bagaimana READ operation dilakukan untuk load game progress? Jelaskan step-by-step.**

53. **Kapan UPDATE operation diperlukan? Berikan contoh skenario dalam game.**

54. **Mengapa DELETE operation perlu confirmation? Bagaimana implement soft delete?**

55. **Jelaskan ACID properties dalam database transaction. Mengapa penting untuk save game?**

#### Data Persistence

56. **Bandingkan JSON file storage vs SQL database untuk save system. Apa kelebihan dan kekurangan masing-masing?**
    - *Jawaban*: JSON: Simple, portable, easy to read. SQL: Structured, queryable, scalable, better for complex relationships.

57. **Bagaimana Jackson library digunakan untuk serialize/deserialize Java objects ke JSON?**

58. **Apa yang dimaksud dengan Object-Relational Mapping (ORM)? Bagaimana bisa diterapkan dalam game ini?**

59. **Jelaskan data migration strategy jika ada perubahan struktur SaveData.**

60. **Bagaimana backup dan recovery strategy untuk player save data?**

---

### BAGIAN 4: IMPLEMENTASI SPESIFIK GAME (Pertanyaan 61-80)

#### Save/Load System

61. **Jelaskan arsitektur save/load system. Class mana saja yang terlibat?**

62. **Bagaimana multiple save slots diimplementasikan? Apa struktur data yang digunakan?**

63. **Mengapa perlu `SaveData` class terpisah dari `Player` class?**

64. **Jelaskan proses serialization dalam `SaveManager.saveGame()`. Field apa saja yang disimpan?**

65. **Bagaimana menangani backward compatibility saat ada perubahan structure SaveData?**

#### Inventory System

66. **Jelaskan data structure yang digunakan untuk Inventory. Mengapa menggunakan Map<String, Integer>?**

67. **Bagaimana menambahkan item baru ke inventory? Apa validation yang dilakukan?**

68. **Jelaskan sorting mechanism dalam inventory. Bagaimana items dikategorikan (Organic, Inorganic, B3)?**

69. **Bagaimana inventory capacity diimplementasikan? Apa yang terjadi saat inventory penuh?**

70. **Jelaskan exchange system di Panjul. Bagaimana item di-convert menjadi gold?**

#### Achievement System

71. **Bagaimana achievement tracking diimplementasikan? Kapan achievement di-unlock?**

72. **Jelaskan data structure untuk menyimpan unlocked achievements. Set vs List, mana yang lebih baik?**

73. **Bagaimana mencegah achievement di-unlock berulang kali?**

74. **Jelaskan trigger mechanism untuk achievement. Siapa yang responsible check achievement conditions?**

75. **Bagaimana achievement data disimpan dan di-restore saat load game?**

#### Map & Portal System

76. **Jelaskan map transition system. Bagaimana loading screen bekerja?**

77. **Bagaimana portal detection bekerja? Tile mana yang dikategorikan sebagai portal?**

78. **Jelaskan automatic map connection system. Bagaimana menentukan destination map berdasarkan direction?**

79. **Bagaimana TMX file di-parse dan di-convert menjadi game objects?**

80. **Jelaskan collision detection system. Bagaimana tile collision berbeda dengan object collision?**

---

### BAGIAN 5: ADVANCED TOPICS & OPTIMIZATION (Pertanyaan 81-100)

#### Performance Optimization

81. **Bagaimana game loop dioptimasi untuk mencapai consistent 50 FPS?**

82. **Jelaskan sprite caching mechanism. Mengapa tidak load sprite setiap frame?**

83. **Bagaimana culling system bekerja? Object mana yang di-render dan mana yang di-skip?**

84. **Apa itu delta time? Bagaimana digunakan untuk smooth animation?**

85. **Jelaskan lazy loading dalam MapManager. Map kapan di-load?**

#### Multithreading

86. **Apakah game ini menggunakan multithreading? Thread mana saja yang berjalan?**

87. **Apa perbedaan synchronous dan asynchronous operation? Contoh dalam game?**

88. **Bagaimana menghindari race condition saat multiple threads akses shared data?**

89. **Jelaskan EDT (Event Dispatch Thread) dalam Swing. Mengapa UI update harus di EDT?**

90. **Bagaimana background loading bisa diimplementasikan untuk map transitions?**

#### Testing & Debugging

91. **Bagaimana melakukan unit testing untuk `SaveManager`? Mock apa yang diperlukan?**

92. **Jelaskan integration testing untuk save/load system. Apa saja test cases?**

93. **Bagaimana debugging force unlock feature (F2) membantu development?**

94. **Apa itu assertion? Bagaimana digunakan untuk validate game state?**

95. **Jelaskan logging strategy. Level logging apa yang digunakan (INFO, DEBUG, ERROR)?**

#### Extensibility & Maintenance

96. **Bagaimana menambah chapter baru dalam game? Class mana yang perlu dimodifikasi?**

97. **Jelaskan plugin architecture. Bagaimana mod system bisa diimplementasikan?**

98. **Bagaimana configuration file (Settings) membantu maintainability?**

99. **Jelaskan version control strategy. Bagaimana menangani merge conflict dalam team development?**

100. **Apa improve yang bisa dilakukan untuk make the codebase more maintainable? Code refactoring apa yang disarankan?**

---

## 📊 DATABASE SCHEMA PROPOSAL

### Table: Players
```sql
CREATE TABLE Players (
    player_id INT PRIMARY KEY AUTO_INCREMENT,
    player_name VARCHAR(50) NOT NULL UNIQUE,
    energy INT NOT NULL DEFAULT 100,
    max_energy INT NOT NULL DEFAULT 100,
    gold INT NOT NULL DEFAULT 500,
    score INT NOT NULL DEFAULT 0,
    current_map INT NOT NULL DEFAULT 5,
    world_x INT NOT NULL,
    world_y INT NOT NULL,
    chapter1_active BOOLEAN DEFAULT TRUE,
    chapter2_active BOOLEAN DEFAULT FALSE,
    chapter2_finished BOOLEAN DEFAULT FALSE,
    chapter3_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Table: SaveSlots
```sql
CREATE TABLE SaveSlots (
    slot_id INT PRIMARY KEY,
    player_id INT,
    save_name VARCHAR(100),
    current_day INT NOT NULL,
    current_minute INT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES Players(player_id) ON DELETE CASCADE
);
```

### Table: Inventory
```sql
CREATE TABLE Inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    player_id INT NOT NULL,
    item_name VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    item_type ENUM('ORGANIC', 'INORGANIC', 'B3', 'SEED') NOT NULL,
    FOREIGN KEY (player_id) REFERENCES Players(player_id) ON DELETE CASCADE,
    UNIQUE KEY unique_player_item (player_id, item_name)
);
```

### Table: Achievements
```sql
CREATE TABLE Achievements (
    achievement_id INT PRIMARY KEY AUTO_INCREMENT,
    achievement_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    category ENUM('TRASH', 'PLANT', 'STORY') NOT NULL
);
```

### Table: PlayerAchievements
```sql
CREATE TABLE PlayerAchievements (
    player_id INT NOT NULL,
    achievement_id INT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id, achievement_id),
    FOREIGN KEY (player_id) REFERENCES Players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES Achievements(achievement_id) ON DELETE CASCADE
);
```

### Table: TrashCollected
```sql
CREATE TABLE TrashCollected (
    collection_id INT PRIMARY KEY AUTO_INCREMENT,
    player_id INT NOT NULL,
    map_name VARCHAR(50) NOT NULL,
    trash_x INT NOT NULL,
    trash_y INT NOT NULL,
    collected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES Players(player_id) ON DELETE CASCADE,
    UNIQUE KEY unique_trash_location (player_id, map_name, trash_x, trash_y)
);
```

---

## 🎓 TIPS PRESENTASI

### Yang Perlu Disiapkan:
1. **Demo Video** - Recording gameplay dari start to finish
2. **ERD Diagram** - Visual representation database schema
3. **Class Diagram** - UML diagram showing OOP relationships
4. **Code Walkthrough** - Siapkan highlight code untuk setiap konsep OOP
5. **Database Queries** - Live demo CRUD operations

### Yang Perlu Dijelaskan:
- **Why Java?** - Cross-platform, mature ecosystem, strong OOP support
- **Why This Design?** - Design decisions dan trade-offs
- **Challenges Faced** - Technical challenges dan bagaimana solved
- **Future Improvements** - Apa yang bisa di-improve next

### Sample Presentation Flow:
1. Introduction & Demo (5 min)
2. Architecture Overview (5 min)
3. OOP Implementation (10 min)
4. Database Design (10 min)
5. Code Walkthrough (10 min)
6. Q&A (10 min)

---

## 📚 REFERENSI & RESOURCES

### Books:
- Head First Design Patterns
- Effective Java by Joshua Bloch
- Database System Concepts by Silberschatz

### Online Resources:
- Oracle Java Documentation
- Refactoring Guru (Design Patterns)
- GeeksforGeeks (OOP Concepts)

---

**Good luck dengan presentasi! 🚀**
