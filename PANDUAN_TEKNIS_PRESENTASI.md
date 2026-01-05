# PANDUAN TEKNIS PRESENTASI: PETUALANGAN HIJAU
## Pembagian Tugas, Pertanyaan Teknis & Kunci Jawaban (OOP & Database)

---

### 1. Navanza Varel (2409181) - Database Developer
**Fokus:** Perancangan ERD, Struktur Database, SQL Queries, Data Integrity.

*   **Q: Mengapa Anda memilih normalisasi 3NF pada tabel `Inventory` dan `PlayerAchievements`?**
    *   **A:** Untuk menghindari redundansi data dan anomali. Misalnya, pada tabel `Inventory`, ID player dipisah dari detail item. Jika nama item berubah, kita tidak perlu mengupdate ribuan baris, cukup pada tabel referensi atau kolom spesifik tanpa merusak relasi antara player dan barang miliknya.
*   **Q: Bagaimana cara Anda menangani relasi Many-to-Many antara `Players` dan `Achievements`?**
    *   **A:** Saya menggunakan tabel perantara (junction table) bernama `PlayerAchievements`. Tabel ini menyimpan `player_id` dan `achievement_id`. Ini menerapkan konsep relasi relasional di mana satu player bisa punya banyak achievement, dan satu tipe achievement bisa dimiliki oleh banyak player.
*   **Q: Jelaskan query SQL untuk menyimpan progress jumlah sampah yang dikumpulkan per area!**
    *   **A:** Saya menggunakan query `INSERT INTO TrashCollected (player_id, map_name, trash_x, trash_y) VALUES (?, ?, ?, ?)`. Saya juga menambahkan constraint `UNIQUE` pada kombinasi area dan koordinat agar player tidak bisa mensubmit sampah yang sama dua kali ke database.
*   **Q: Bagaimana cara memastikan data tidak hilang (Data Integrity) saat game tiba-tiba tertutup saat saving?**
    *   **A:** Saya menerapkan sistem Transaction (BEGIN, COMMIT, ROLLBACK). Jika proses penulisan ke database terinterupsi di tengah jalan, database akan melakukan ROLLBACK secara otomatis sehingga data kembali ke kondisi save terakhir yang utuh dan tidak "setengah jadi" (corrupted).
*   **Q: Apa perbedaan teknis antara menyimpan data di file JSON (SaveManager saat ini) dengan Database Relasional yang Anda rancang?**
    *   **A:** File JSON bersifat flat dan sulit untuk di-query (misalnya mencari player dengan gold terbanyak). Database relasional memungkinkan kita melakukan indexing dan filter data yang kompleks dengan cepat menggunakan SQL, serta lebih aman dalam menangani relasi antar entitas yang rumit.

---

### 2. Nurjiha (2400515) - Gameplay Logic Programmer
**Fokus:** Game Loop, Collision Detection, Pathfinding, Energy/Score System.

*   **Q: Bagaimana konsep Encapsulation diterapkan pada sistem Energy player?**
    *   **A:** Variabel `energy` dideklarasikan sebagai `private`. Untuk menguranginya (misal saat memungut sampah), class lain harus memanggil method `loseEnergy()`. Di dalam method tersebut, saya menambahkan logika pengecekan: jika energy < 5, maka otomatis memicu state `Exhaustion`. Class lain tidak bisa sembarangan mengubah nilai energy tanpa melewati filter logika ini.
*   **Q: Jelaskan cara kerja Collision Detection menggunakan class `Rectangle` di Java!**
    *   **A:** Setiap Entity memiliki `hitbox` (objek Rectangle). Dalam class `CollisionChecker`, saya menggunakan method `.intersects()`. Logikanya: saya memprediksi posisi x/y entity di frame berikutnya, lalu mengecek apakah objek Rectangle di posisi baru tersebut bertabrakan dengan solid tile atau hitbox objek lain.
*   **Q: Mengapa Anda menggunakan Singleton Pattern pada `TimeManager`?**
    *   **A:** Karena waktu dalam game harus bersifat global dan sinkron. Dengan Singleton, kita menjamin hanya ada satu "jam" yang berjalan. `NPCManager`, `UIManager`, dan `Player` semuanya mereferensikan instance yang sama, sehingga transisi siang/malam terjadi bersamaan untuk semua komponen.
*   **Q: Bagaimana logika Pathfinding (A*) bekerja untuk NPC seperti Pak Bahlil saat dia harus berjalan ke koordinat tertentu?**
    *   **A:** Algoritma A* mencari jalur terpendek dengan menjumlahkan 'G cost' (jarak dari start) dan 'H cost' (estimasi jarak ke finish). Logic ini saya implementasikan dalam class `Pathfinding`. NPC akan mengecek tile tetangga, menghindari tile solid, dan memilih tile dengan total cost terendah secara repetitif hingga sampai ke tujuan.
*   **Q: Jelaskan implementasi Polymorphism pada method `update()` di game engine ini!**
    *   **A:** Class `GamePanel` memanggil `entity.update()` di dalam loop utama. Meskipun tipe aslinya bisa berupa `Player`, `NPC`, atau `Panjul`, karena mereka semua inherit dari `Entity`, kita bisa memanggil method update yang sama namun dengan perilaku yang berbeda-beda (Dynamic Method Dispatch).

---

### 3. Faiz Bisma Alfarid (2407903) - Visual Artist & Content Designer
**Fokus:** Resource Loading, Sprite Management, Animation Logic, Rendering Efficiency.

*   **Q: Bagaimana Anda mengoptimasi loading banyak sprite agar tidak memakan RAM berlebih (OOM Error)?**
    *   **A:** Saya menggunakan teknik Sprite Caching. Gambar di-load hanya sekali saat inisialisasi menggunakan `ImageIO.read()` dan disimpan dalam variabel static atau di-cache dalam Map. Saat rendering, kita hanya mereferensikan objek gambar yang sudah ada di memori, bukan me-load-nya berulang kali setiap frame.
*   **Q: Jelaskan logika OOP di balik sistem animasi NPC yang berganti gambar setiap beberapa detik!**
    *   **A:** Saya menggunakan variabel `spriteCounter` dan `spriteNum` di class `Entity`. Setiap kali game loop berjalan, counter bertambah. Jika counter mencapai batas tertentu (misal 15), `spriteNum` akan berganti (1 ke 2, atau sebaliknya). Di method `draw()`, program memilih file gambar berdasarkan nilai `spriteNum` tersebut.
*   **Q: Bagaimana cara sistem rendering membedakan layer antara tanah, objek, dan NPC?**
    *   **A:** Ini dilakukan melalui urutan pemanggilan method `draw()` di `GamePanel`. Pertama kita gambar tile map (layer bawah), lalu objek statis, kemudian NPC dan Player. Dengan urutan ini, object yang digambar terakhir akan tampak "di atas" object yang digambar sebelumnya (Z-ordering).
*   **Q: Apa yang dilakukan program jika file sprite (misalnya `background.png`) hilang atau rusak?**
    *   **A:** Saya mengimplementasikan Error Handling dengan blok `try-catch`. Jika `ImageIO.read()` gagal, program akan menangkap exception tersebut dan mengarahkan ke method `createProgrammaticBackground()` yang secara dinamis menggambar warna gradien menggunakan `Graphics2D` sebagai fallback agar game tidak crash.
*   **Q: Bagaimana Anda mengatur resolusi sprite agar tetap proporsional saat game dijalankan dalam mode Fullscreen?**
    *   **A:** Saya menggunakan variabel `tileSize` yang bersifat dinamis. Semua koordinat dan ukuran gambar dikalikan dengan variabel ini. Saat masuk mode fullscreen, program menghitung skala layar dan memperbesar `tileSize` sehingga semua visual ikut membesar secara proporsional.

---

### 4. Nur Adila Putri (2402926) - UI/UX Designer
**Fokus:** Interface Logic, Menus, Dialog Boxes, Interaction Feedback.

*   **Q: Bagaimana class `UI` memanfaatkan Abstraksi untuk mendukung berbagai jenis layar (Title, Save, Inventory)?**
    *   **A:** Class `UI` berfungsi sebagai parent class abstrak yang menyediakan fungsi umum seperti `drawCenteredText()`, `getHorizontalCenter()`, dan manajemen font. Layar spesifik seperti `TitleScreen` tinggal mewarisi fungsi-fungsi ini dan hanya fokus pada konten unik masing-masing (Implementation Hiding).
*   **Q: Jelaskan logika di balik sistem "Interaction Hint" (muncul tulisan [E] Masuk) saat player mendekati pintu!**
    *   **A:** Di class `Player` method `update()`, program terus mengecek jarak player ke objek terdekat. Jika jarak < `tileSize`, variabel `nearObjectType` diset. Di method `draw()`, jika variabel tersebut tidak null, UI akan merender string hint di posisi koordinat objek tersebut di layar.
*   **Q: Bagaimana sistem dialog menangani teks yang panjang agar tidak terpotong (Clipping)?**
    *   **A:** Saya menggunakan karakter khusus `\\n` sebagai line break manual. Method `drawDialog()` akan membagi string berdasarkan karakter tersebut menjadi array of strings, lalu melakukan loop untuk menggambar setiap baris dengan offset koordinat Y yang berbeda.
*   **Q: Bagaimana class `SaveLoadScreen` mengambil data dari database untuk ditampilkan di slot save-an?**
    *   **A:** Saat screen dibuka, method `refreshSlotCache()` dipanggil. Ia berkomunikasi dengan `SaveManager` (Controller) yang melakukan query SELECT ke database. Data tersebut (Nama, Day, Gold) disimpan dalam array objek `SaveData` untuk kemudian digambar ke layar oleh class UI.
*   **Q: Jelaskan implementasi State Pattern pada UI saat player menekan tombol ESC!**
    *   **A:** Saat ESC ditekan, `InputManager` mengubah nilai di `StateManager`. Objek UI akan mendeteksi perubahan state tersebut (misal dari PLAY ke PAUSE). Method `draw()` pada UI selalu menggunakan switch-case berdasarkan state saat ini untuk menentukan interface mana yang harus tampil.

---

### 5. M. Randy Kurniawan (2405315) - PM: Story & Logic Designer
**Fokus:** State Management, Overall Architecture, Feature Integration, Progress Tracking.

*   **Q: Jelaskan arsitektur keseluruhan game ini! Bagaimana Anda memisahkan logic, data, dan tampilan (konsep Separation of Concerns)?**
    *   **A:** Proyek ini menggunakan arsitektur modular. Folder `model` menyimpan data (Player, Map, Entity), `view` menyimpan presentasi visual (TitleScreen, UI), dan `controller` mengatur logic (GamePanel, InputManager, StateManager). Ini memudahkan maintenance karena perubahan pada tampilan tidak akan merusak logic database.
*   **Q: Bagaimana Anda mengoordinasikan interaksi antara `InventorySystem` dengan `ChapterProgress` agar story bisa berlanjut?**
    *   **A:** Saya menggunakan variabel flag di `GamePanel` (misal `chapter1TrashCount`). Setiap kali logic `addItem()` di inventory terpanggil dan itemnya adalah sampah, ia akan menaikkan counter tersebut. Jika counter mencapai target, logic programmer akan memicu state `CUTSCENE` untuk melanjutkan story.
*   **Q: Mengapa game ini membutuhkan `StateManager` yang kompleks? Mengapa tidak menggunakan variabel boolean sederhana?**
    *   **A:** Karena game memiliki banyak kondisi yang bersifat eksklusif (tidak bisa lari saat sedang dialog, tidak bisa buka inventory saat pause). Menggunakan enum `gameState` dan `StateManager` memastikan hanya ada satu state aktif di satu waktu, mencegah bug tumpang tindih input dan rendering.
*   **Q: Bagaimana Anda memastikan sinkronisasi antara logic Story (Chapter 1, 2, 3) saat player melakukan Save dan Load?**
    *   **A:** Semua variabel progress story (boolean flags dan counters) dimasukkan ke dalam class `PlayerData` yang merupakan bagian dari `SaveData`. Saat loading, `SaveManager` mendistribusikan kembali nilai-masing-masing variabel tersebut ke `GamePanel`, sehingga dunia game kembali tepat ke kondisi saat story ditinggalkan.
*   **Q: Jelaskan peran method `forceUnlock()` (F2) dari sisi manajemen proyek dan koordinasi logic!**
    *   **A:** Itu adalah mekanisme "fail-safe". Dalam game dengan banyak state dan cutscene, ada risiko deadlock (player terjebak tidak bisa gerak). Method ini memaksa mereset semua state kembali ke PLAY. Ini adalah implementasi "Robustness Principle" dalam desain perangkat lunak untuk menjaga user experience.

---

### 6. M. Khairul Irham Masruri (2402023) - Gameplay Story Programmer
**Fokus:** Cutscene System, NPC Conversations, Quest Triggers, Language Localization.

*   **Q: Bagaimana sistem dialog mengambil teks dari file external (`dialog_id.txt`)? Jelaskan logic OOP-nya!**
    *   **A:** Saya menggunakan `StoryManager` yang membaca file teks dan menyimpannya dalam `Map<String, String>`. Saat NPC berinteraksi, ia mengirim "key" (misal `saman_greeting`). Program kemudian mencari value string yang sesuai di Map tersebut. Ini menerapkan konsep Abstraksi Data di mana teks tidak di-hardcode di dalam class Java.
*   **Q: Jelaskan cara kerja `CutsceneManager` dalam mengatur urutan kejadian (Sequential Events)!**
    *   **A:** Cutscene menggunakan sistem "Step" atau "Phase". Saya menggunakan switch-case yang dikontrol oleh timer atau input player. Contoh: Step 1 (NPC bicara), Step 2 (NPC jalan), Step 3 (Layar Fade Out). State ini mencegah player mengontrol karakter selama urutan story berlangsung.
*   **Q: Bagaimana Anda mengimplementasikan sistem lokalisasi (Bahasa Indonesia & Inggris) secara teknis?**
    *   **A:** Di class `Settings`, terdapat variabel `gameLanguage`. Saat `StoryManager` melakukan inisialisasi, ia mengecek variabel ini dan memilih file mana yang akan di-load (`dialog_id.txt` atau `dialog_en.txt`). Seluruh sistem dialog menggunakan key yang sama, sehingga pergantian bahasa sangat seamless tanpa mengubah logic code.
*   **Q: Bagaimana logic story mendeteksi bahwa player sudah memberikan bibit pohon ke Teh Dila agar dialog berganti?**
    *   **A:** Dalam method `interact()` di class `NPC`, terdapat pengecekan condition terhadap variabel global di `GamePanel` (seperti `tehDilaGiftGiven`). Ini adalah contoh interaksi antar objek (Object Interaction) di mana status quest menentukan branch dialog mana yang akan diambil oleh objek NPC.
*   **Q: Jelaskan implementasi "Narrative Overlay" (teks cerita hitam yang muncul di awal chapter)!**
    *   **A:** Itu dikelola oleh `StoryScreen` yang inherit dari class `UI`. Logikanya menggunakan efek Typewriter (teks muncul satu per satu). Saya menggunakan `substring()` di Java untuk menampilkan teks dari index 0 hingga index yang terus bertambah setiap frame, memberikan kesan tulisan sedang diketik.
