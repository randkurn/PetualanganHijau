# Sistem Portal dengan Loading Screen

## Deskripsi
Sistem portal yang mendeteksi tile-tile tertentu untuk melakukan perpindahan antar map dengan animasi loading screen dan konten edukatif tentang sampah dan banjir di Indonesia.

## Konfigurasi Portal Tiles

### Tile ID Portal
- **Tile 28**: Portal standar (digunakan di Farm, dan ID portal umum)
- **Tile 308**: Portal untuk House dan City
- **Tile 469**: Portal khusus untuk Park

## Logika Perpindahan Map

Berikut adalah struktur perpindahan antar map berdasarkan arah:

### Map: House (Index 0)
- **Kanan** → City (Index 3)
- **Bawah** → Fishing (Index 4)
- **Atas** → Park (Index 2)

### Map: City (Index 3)
- **Kiri** → House (Index 0)

### Map: Fishing (Index 4)
- **Atas** → House (Index 0)
- **Bawah** → Farm (Index 1)

### Map: Farm (Index 1)
- **Atas** → Fishing (Index 4)
- **Bawah** → Park (Index 2)

### Map: Park (Index 2)
- **Atas** → Farm (Index 1)
- **Bawah** → House (Index 0) *(loop kembali)*

## Fitur Loading Screen

### Animasi Loading
- Progress bar yang menunjukkan loading progress
- Animasi titik-titik (...) yang bergerak
- Persentase loading yang real-time

### Konten Edukatif
Setiap loading screen menampilkan konten edukatif acak tentang:

#### Topik Sampah
- Fakta tentang produksi sampah di Indonesia
- Bahaya plastik dan waktu penguraiannya
- Dampak sampah organik yang tidak dikelola
- Tips reduce, reuse, recycle

#### Topik Banjir
- Hubungan sampah dengan banjir
- Statistik banjir di Indonesia
- Cara mencegah banjir
- Dampak ekonomi dan sosial banjir

### Contoh Konten Edukatif
```
"Indonesia menghasilkan 64 juta ton sampah per tahun, dan hanya 10% yang didaur ulang!"

"Sampah yang menumpuk di sungai menyumbat aliran air dan menyebabkan banjir!"

"Membuang sampah pada tempatnya dapat mengurangi risiko banjir hingga 40%."
```

## Penggunaan di Kode

### 1. PortalSystem Class
Kelas utama yang mengelola deteksi portal dan loading screen.

```java
// Di dalam GamePanel
public PortalSystem portalSystem = new PortalSystem(this);

// Update di game loop
portalSystem.update();

// Render loading screen
portalSystem.draw(g2);
```

### 2. Deteksi Portal di Player
Portal secara otomatis terdeteksi saat player berjalan di atas tile portal.

```java
// Auto-detect portal tiles untuk seamless transitions
if (gamePanel.portalSystem != null && !gamePanel.portalSystem.isLoading()) {
    gamePanel.portalSystem.checkPortalTile(worldX, worldY);
}
```

### 3. Integrasi dengan GamePanel
- Portal System di-update setiap frame
- Loading screen di-render di atas semua elemen lain
- Player movement diblokir selama loading

## Cara Kerja

1. **Deteksi**: Player berjalan ke tile portal (28, 308, atau 469)
2. **Analisis Arah**: Sistem menentukan arah perpindahan berdasarkan posisi player di map
3. **Tentukan Map Tujuan**: Berdasarkan map saat ini dan arah, tentukan map tujuan
4. **Mulai Loading**: Tampilkan loading screen dengan konten edukatif acak
5. **Progress**: Loading bar dan animasi berjalan
6. **Perpindahan**: Setelah loading selesai, pindah ke map baru
7. **Reset**: Loading screen fade out dan player bisa bergerak lagi

## Modifikasi dan Kustomisasi

### Menambah Konten Edukatif
Edit method `initEducationalContent()` di `PortalSystem.java`:

```java
educationalContent.add(new EducationalContent(
    "Judul Konten",
    "Deskripsi konten edukatif yang akan ditampilkan...",
    Color.GREEN  // Warna box
));
```

### Mengubah Waktu Loading
Edit nilai `loadingProgress` increment di method `update()`:

```java
// Loading lebih cepat
loadingProgress += 0.05f;  

// Loading lebih lambat
loadingProgress += 0.01f;
```

### Menambah Portal di Map Baru
1. Tambahkan map index konstanta di `PortalSystem.java`
2. Update method `determineNextMap()` dengan logika portal untuk map baru
3. Pastikan TMX file menggunakan tile ID yang benar

## Troubleshooting

### Portal tidak berfungsi
- Cek apakah tile ID di TMX file sesuai (28, 308, atau 469)
- Pastikan `PortalSystem` sudah di-instantiate di `GamePanel`
- Cek apakah method `checkPortalTile()` dipanggil di `Player.update()`

### Loading Screen tidak muncul
- Pastikan `portalSystem.draw(g2)` dipanggil di `GamePanel.paintComponent()`
- Cek apakah loading di-render setelah elemen game lain

### Player masih bisa bergerak saat loading
- Pastikan pengecekan `portalSystem.isLoading()` ada di `Player.update()`
- Cek apakah return statement terpanggil saat loading aktif

## File yang Dimodifikasi

1. **PortalSystem.java** (BARU) - Sistem portal utama
2. **GamePanel.java** - Integrasi portal system
3. **Player.java** - Deteksi portal dan blocking movement saat loading
4. **TMX Files** - Update portal tiles dengan ID yang benar (akan dilakukan manual)

## Roadmap

### Implementasi Selanjutnya
- [ ] Update semua TMX file dengan tile portal yang benar
- [ ] Tambah sound effect saat portal
- [ ] Animasi visual saat transisi portal
- [ ] Tracking statistik perpindahan map
- [ ] Portal cooldown untuk mencegah trigger berulang
