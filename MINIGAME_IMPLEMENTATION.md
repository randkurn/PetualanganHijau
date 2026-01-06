# MINIGAME & PATROL SYSTEM IMPLEMENTATION

## ✅ **MINIGAMES CREATED**

### **1. Drainage Minigame** (`DrainageMinigame.java`)
**For:** Bisma - Cleaning clogged drainage

**How to Play:**
- Click on brown trash piles (marked with red X)
- Remove 5 trash piles to complete
- Used in: `model/Bisma.java`

**Features:**
- Simple click-based interaction
- Visual feedback for clicks
- Progress counter
- Auto-close after 2 seconds on completion

**Integration:**
```java
// In Bisma.java interact() method:
DrainageMinigame minigame = new DrainageMinigame(gp);
minigame.start(() -> {
    // Completion callback
    hasInteracted = true;
    gp.player.addGold(1000);
    // Show reward message
});
```

---

### **2. River Catch Minigame** (`RiverCatchMinigame.java`)
**For:** Randy - Cleaning polluted river

**How to Play:**
- Use **WASD** to move the green net
- Catch gray **trash** (good!)
- **Avoid** orange fish (penalty: -1 trash count)
- Catch 10 trash to complete

**Features:**
- Side-scrolling gameplay
- Spawns trash and fish dynamically every second
- Movement controls with net
- Collision detection
- Fish penalty system

**Integration:**
```java
// In Randy.java interact() method:
RiverCatchMinigame minigame = new RiverCatchMinigame(gp);
minigame.start(() -> {
    // Completion callback
    hasInteracted = true;
    gp.player.addGold(1500);
    // Give "Ikan Segar" reward
});
```

---

## **3. PAK KHAIRUL PATROL SYSTEM**

### **Waypoint Information (from farm.tmx):**

| Waypoint | Object Name | X | Y | Description |
|----------|-------------|---|---|-------------|
| 1 (Start) | farmer1 | 1189 | 198 | Top Right Corner (Starting position) |
| 2 | farmer2 | 1184 | 1106 | Bottom Right Corner |
| 3 | farmer3 | 268 | 1107 | Bottom Left Corner |
| 4 | farmer4 | 267 | 208 | Top Left Corner |

### **Patrol Route:**
```
farmer1 (1189,198)  →  farmer2 (1184,1106)
        ↑                       ↓
farmer4 (267,208)   ←  farmer3 (268,1107)
```

**Loop:** Repeats indefinitely during Chapter 3

### **Implementation Needed in PakKhairul.java:**

```java
// Add to PakKhairul class:
private boolean isPatrolling = true;
private int currentWaypoint = 0;
private int[] waypointX = {1189, 1184, 268, 267};
private int[] waypointY = {198, 1106, 1107, 208};
private final int WAYPOINT_THRESHOLD = 16; // distance to reach waypoint
private int patrolSpeed = 2;

// In update() method:
public void update() {
    if (isPatrolling && !hasInteracted) {
        // Get target waypoint
        int targetX = waypointX[currentWaypoint];
        int targetY = waypointY[currentWaypoint];
        
        // Calculate distance
        int dx = targetX - worldX;
        int dy = targetY - worldY;
        double distance = Math.sqrt(dx*dx + dy*dy);
        
        // If reached waypoint, move to next
        if (distance < WAYPOINT_THRESHOLD) {
            currentWaypoint = (currentWaypoint + 1) % waypointX.length;
        } else {
            // Move towards target
            double angle = Math.atan2(dy, dx);
            worldX += (int)(Math.cos(angle) * patrolSpeed);
            worldY += (int)(Math.sin(angle) * patrolSpeed);
            
            // Update direction for sprite
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }
        }
    }
    
    // Animate sprite
    spriteCounter++;
    if (spriteCounter > 30) {
        spriteNum = (spriteNum == 1) ? 2 : 1;
        spriteCounter = 0;
    }
}
```

---

## **INTEGRATION STEPS**

### **Step 1: Add Minigames to GamePanel**
```java
// In GamePanel.java:
public DrainageMinigame drainageMinigame;
public RiverCatchMinigame riverMinigame;

// In constructor:
drainageMinigame = new DrainageMinigame(this);
riverMinigame = new RiverCatchMinigame(this);

// In update():
if (drainageMinigame.is Active()) {
    drainageMinigame.update();
} else if (riverMinigame.isActive()) {
    riverMinigame.update();
}

// In draw():
if (drainageMinigame.isActive()) {
    drainageMinigame.draw(g2);
} else if (riverMinigame.isActive()) {
    riverMinigame.draw(g2);
}
```

### **Step 2: Add Mouse Listener for Drainage Minigame**
```java
// In GamePanel or InputHandler:
public void mouseClicked(MouseEvent e) {
    if (gp.drainageMinigame.isActive()) {
        gp.drainageMinigame.handleClick(e.getX(), e.getY());
    }
}
```

### **Step 3: Update NPC Interactions**

**Bisma.java:**
```java
// In interact() method, replace TODO:
gp.drainageMinigame.start(() -> {
    hasInteracted = true;
    gp.player.addGold(1000);
    gp.uiM.showMessage("Drainase lancar! +1000 Gold");
});
```

**Randy.java:**
```java
// In interact() method, replace TODO:
gp.riverMinigame.start(() -> {
    hasInteracted = true;
    gp.player.addGold(1500);
    // Add fish to inventory
    gp.player.inventory.addItem("Ikan Segar", 3, fishIcon);
    gp.uiM.showMessage("Sungai bersih! +1500 Gold, +3 Ikan Segar");
});
```

---

## **TESTING CHECKLIST**

- [ ] Drainage Minigame launches from Bisma interaction
- [ ] Can click and remove all 5 trash piles
- [ ] Receives reward after completion
- [ ] River Minigame launches from Randy interaction
- [ ] Can move net with WASD
- [ ] Trash increases counter, fish decreases it
- [ ] Receives reward after catching 10 trash
- [ ] Pak Khairul patrols between 4 waypoints continuously
- [ ] Can still interact with Pak Khairul while patrolling
- [ ] All minigames close properly and return to PLAY state

---

## **FILES CREATED:**
1. ✅ `src/main/java/view/DrainageMinigame.java`
2. ✅ `src/main/java/view/RiverCatchMinigame.java`

## **FILES TO MODIFY:**
1. `src/main/java/controller/GamePanel.java` - Add minigame instances
2. `src/main/java/model/Bisma.java` - Integrate drainage minigame
3. `src/main/java/model/Randy.java` - Integrate river minigame
4. `src/main/java/model/PakKhairul.java` - Add patrol system
5. Input handling - Add mouse click listener

---

**Status: MINIGAMES READY FOR INTEGRATION!** 🎮
