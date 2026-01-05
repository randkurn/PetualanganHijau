package model;

import javax.imageio.ImageIO;
import controller.GamePanel;

public class OBJ_TreeSeed extends GameObject {

    public OBJ_TreeSeed() {
        super(Type.DECORATION);
        this.displayName = "Bibit Pohon";
        this.description = "Tanam untuk menghijaukan lingkungan!";
        this.collision = false;
        this.pickable = true;
        this.goldValue = 10;

        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/tree1.png"));
            System.out.println("Tree seed icon loaded from tree1.png");
        } catch (Exception e) {
            System.err.println("Failed to load tree seed image: " + e.getMessage());
        }
    }

    @Override
    public void interact(GamePanel gp) {
        if (gp.player.inventory.addItem(displayName, 1, image, null)) {
            gp.uiM.showMessage("Diperoleh: " + displayName);
            gp.mapM.getMap().objects[index] = null;
        } else {
            gp.uiM.showMessage("Inventory penuh!");
        }
    }
}
