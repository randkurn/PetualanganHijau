# CHAPTER 4 & 5 - COMPLETE IMPLEMENTATION GUIDE

## 📋 OVERVIEW

**Chapter 4: Peringatan Badai (The Warning)**
- Phone call system
- Chase minigame (Ariel)
- Timed cleanup event (3 minutes)
- EnviroMeter target: 50%

**Chapter 5: Titik Nadir (The Last Stand)**
- Ultimate challenge: < 30% trash, > 15 trees
- Weather system (heavy rain)
- Ariel follower system
- Multiple endings (Bad/Good)
- Credits roll

---

## 🎯 NEW SYSTEMS REQUIRED

### 1. Phone Call System
```java
class PhoneCallManager {
    - Display phone UI overlay
    - Show caller name & message
    - Trigger quest updates
}
```

### 2. Chase Minigame
```java
class ChaseMinigame {
    - Auto-running player
    - Obstacles to dodge
    - SPACE to catch at end
    - Timer/distance tracking
}
```

### 3. Timed Cleanup Event
```java
class TimedCleanupEvent {
    - 3 minute countdown
    - Real-time EnviroMeter tracking
    - Multiple NPCs helping
    - Success/fail conditions
}
```

### 4. Weather System
```java
class WeatherManager {
    - Rain effects (light/heavy)
    - Screen darkening
    - Movement speed modifier
    - Visual overlays
}
```

### 5. NPC Follower System
```java
class NPCFollower {
    - Follow player pathfinding
    - Auto-pickup trash
    - Unlimited inventory capacity
}
```

### 6. Ending System
```java
class EndingManager {
    - Check final stats
    - Trigger appropriate ending
    - Credits roll
    - Game completion flag
}
```

---

## 📝 DIALOGUE ENTRIES (dialog_id.txt)

### Chapter 4 Dialogues
```
ch4_panjul_call_1: Halo Bro! Gawat nih!
ch4_panjul_call_2: BMKG barusan rilis info, sore ini bakal ada badai super.
ch4_panjul_call_3: Ada yang sengaja buang sampah segunung di Fishing Area!
ch4_randy_chase: Woy! Itu dia orangnya, Bro! Kejar!

ch4_ariel_caught_1: Lepasin! Gue cuma mau beresin gudang bokap gue!
ch4_ariel_caught_2: Gue... gue gak mikir sampe situ. Maafin gue, Bang.

ch4_panjul_urgent_1: Udah gak ada waktu buat minta maaf! Air udah naik!
ch4_panjul_urgent_2: Kalau kita gak turunin sampah sampe 50%, drainase bakal jebol!
ch4_ariel_help: Oke, gue bantuin! Gue angkutin yang gede-gede!

ch4_panjul_warning_1: Kita berhasil nahan di angka 50%.
ch4_panjul_warning_2: Tapi ini baru pemanasan. Badai beneran dateng besok.
ch4_panjul_warning_3: Besok adalah kesempatan terakhir.

ch4_monolog_end_1: Untung tadi sempet keburu 50%.
ch4_monolog_end_2: Tapi besok... tantangannya bakal jauh lebih gila.

ch4_narrative_1: Langit marah, memberi tanda.
ch4_narrative_2: Waktu menipis, tersisa doa.
ch4_narrative_3: Esok hari, nasib kota dipertaruhkan.
```

### Chapter 5 Dialogues
```
ch5_ibu_worried: Hujannya ngeri banget, Nak... Ibu takut banjirnya masuk rumah.
ch5_monolog_start: Tenang, Bu. Aku gak bakal biarin air masuk.

ch5_panjul_briefing_1: Akhirnya bangun juga lu! Liat tuh air!
ch5_ariel_ready: Bro, gue udah siap nebus dosa. Lu fokus mungut sama nanam pohon.
ch5_panjul_briefing_2: Target kita: Sampah < 30%, Pohon > 15!

ch5_bisma_help: Woy! Jangan kasih kendor! Gue bantu bersihin sumbatan sini!
ch5_jia_help: Kakak semangat! Jia bantu pungutin plastik di taman!
ch5_panjul_radio: Waktu tinggal dikit lagi! Ayo tekan terus!

ch5_panjul_final: Waktu habis... Badai puncak dateng.

# Bad Ending
ch5_bad_panjul: Gagal... Kita telat, Bro. Airnya gak ketahan.
ch5_bad_narrative_1: Alam sudah memberi peringatan berkali-kali.
ch5_bad_narrative_2: Namun kita terlambat untuk peduli.

# Good Ending  
ch5_good_ariel: Liat! Airnya... airnya gak naik lagi!
ch5_good_panjul: Sistem drainase berfungsi 100%! Kota kita selamat!
ch5_good_ariel_thanks: Makasih ya, Bro. Lu udah ngajarin gue arti tanggung jawab.

ch5_narrative_1: Hujan boleh turun deras.
ch5_narrative_2: Tapi tekad untuk berubah, jauh lebih kuat.
ch5_narrative_3: Terima kasih, Pahlawan Lingkungan.
```

---

## 🔧 IMPLEMENTATION PHASES

### Phase 1: Dialogue & Story Setup ✅
- Add all dialogue entries
- Create story flags in GamePanel
- Setup chapter progression logic

### Phase 2: Phone Call System
- Create PhoneCallUI class
- Add to UIManager
- Trigger from cutscenes

### Phase 3: Chase Minigame
- Create ChaseMinigame class
- Auto-runner mechanics
- Obstacle generation
- Catch sequence

### Phase 4: Timed Cleanup Event
- Create TimedEvent system
- 3-minute countdown
- Multi-NPC coordination
- EnviroMeter integration

### Phase 5: Weather System
- Rain particle effects
- Screen overlay darkening
- Movement modifiers
- Audio changes

### Phase 6: Chapter 4 Cutscenes
- Phone call intro
- Chase sequence
- Cleanup event trigger
- Warning ending

### Phase 7: Chapter 5 Gameplay
- Ultimate challenge setup
- Follower system (Ariel)
- Real-time stat tracking
- Final countdown

### Phase 8: Ending System
- Stat evaluation
- Bad ending cutscene (phases 3000-3005)
- Good ending cutscene (phases 4000-4005)
- Credits roll

---

## 📊 GAMEPANEL NEW FIELDS

```java
// Chapter 4 & 5 flags
public boolean chapter4Active = false;
public boolean chapter5Active = false;
public boolean arielCaught = false;
public boolean cleanupEventComplete = false;
public boolean finalChallengeActive = false;

// Stats tracking
public int finalTrashPercentage = 0;
public int fin alTreeCount = 0;
public boolean goodEndingAchieved = false;
```

---

## 🎮 CUTSCENE PHASES ALLOCATION

**Chapter 4:**
- 2000-2010: Phone call & briefing
- 2011-2020: Chase minigame
- 2021-2030: Cleanup event
- 2031-2040: Warning ending

**Chapter 5:**
- 2500-2510: Morning briefing
- 2511-2520: Final challenge active
- 3000-3010: Bad ending sequence
- 4000-4010: Good ending sequence
- 4011-4020: Credits roll

---

## ⚙️ INTEGRATION CHECKLIST

- [ ] Add dialogue entries to dialog_id.txt
- [ ] Create PhoneCallUI.java
- [ ] Create ChaseMinigame.java
- [ ] Create TimedCleanupEvent.java
- [ ] Create WeatherManager.java
- [ ] Create NPCFollower.java (Ariel helper)
- [ ] Add Chapter 4 cutscene phases to CutsceneManager
- [ ] Add Chapter 5 cutscene phases to CutsceneManager
- [ ] Create EndingManager.java
- [ ] Add credits roll UI
- [ ] Update GamePanel with new flags
- [ ] Update SaveManager to handle chapters 4 & 5
- [ ] Test progression: Ch3 → Ch4 → Ch5 → Ending

---

## 🚨 CRITICAL NOTES

1. **Preserve Existing Chapters**: All changes must not break Ch1-3
2. **Modular Design**: Each new system should be standalone
3. **Performance**: Weather effects must be optimized
4. **Save Compatibility**: Must handle partial completion
5. **Testing**: Each chapter must be testable independently

---

## 🎯 SUCCESS CRITERIA

**Chapter 4 Complete:**
- Phone call system works
- Chase minigame playable
- Timed event reaches 50% target
- Transitions to Chapter 5

**Chapter 5 Complete:**
- Weather effects visible
- Follower system functional
- Stats accurately tracked
- Both endings triggerable
- Credits roll properly

**GAME 100% COMPLETE!** 🎉

