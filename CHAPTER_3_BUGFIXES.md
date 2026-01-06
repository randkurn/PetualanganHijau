# CHAPTER 3 - BUG FIXES & NPC COORDINATES UPDATE

## CRITICAL BUGS TO FIX:

### 1. EnviroMeter Calculation Bug
**Problem:** Shows 100% when trash is still high
**Root Cause:** Calculation formula is INVERTED

Current (WRONG):
```java
trashPercent = (trashRemaining / totalTrashInWorld) * 100
// If 85% trash remains → shows 85% (should show 15% clean)
```

Should be:
```java  
trashPercent = ((totalTrashInWorld - trashCollected) / totalTrashInWorld) * 100
// Or better: show CLEANLINESS not DIRTINESS
cleanPercent = (trashCollected / totalTrashInWorld) * 100
```

### 2. Save File Chapter Label
**Problem:** Chapter 2 save shows "Chapter 2" in load screen
**Fix:** After Chapter 2 finished, save should show "Chapter 3"

### 3. Chapter 3 Load Spawn Location  
**Problem:** Player spawns in bedroom
**Fix:** Should spawn in City (near Bisma)

---

## NPC COORDINATES FROM TMX FILES:

### A. **house_interior.tmx**
Spawn Points:
- **PlayerStart (Chapter 1)**: (99, 111.833) → Tiles (6, 6)
- **SpawnUtama**: (247.667, 341.667) → Tiles (15, 21)

**Chapter 3 Wake Up**: Use PlayerStart = (6 * 48, 6 * 48) = (288, 288) in pixels

---

### B. **city.tmx** (Need to check for Bisma location)
- Check object layer for "bisma" spawn point

---

### C. **park.tmx** (Need to check for Neng Jia)
- Check object layer for "neng_jia" spawn point

---

### D. **farm.tmx** (Pak Khairul patrol route)
Patrol Points:
- farmer1: Starting point
- farmer2: Second waypoint  
- farmer3: Third waypoint
- farmer4: Fourth waypoint
Loop: farmer1 → farmer2 → farmer3 → farmer4 → farmer1 (repeat)

---

### E. **fishing.tmx** (Randy + Water Trash)
- Randy spawn point
- **Trash ID 24**: In water (uncollectable)
- **Trash ID 30**: In water (uncollectable)

**Fix**: Mark these trash items as blocked by water collision

---

## IMPLEMENTATION CHECKLIST:

- [ ] Fix EnviroMeter calculation (invert or change to cleanliness %)
- [ ] Update SaveLoadScreen to show "Chapter 3" for finished Chapter 2
- [ ] Update load game spawn: if Chapter 3 active → spawn in City
- [ ] Extract Bisma coordinates from city.tmx
- [ ] Extract Neng Jia coordinates from park.tmx  
- [ ] Extract Pak Khairul patrol route from farm.tmx
- [ ] Extract Randy coordinates from fishing.tmx
- [ ] Block trash ID 24 & 30 in fishing map (water collision)
