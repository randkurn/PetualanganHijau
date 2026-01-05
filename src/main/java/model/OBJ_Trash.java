package model;

import controller.GamePanel;
import java.util.Random;
import javax.imageio.ImageIO;

public class OBJ_Trash extends GameObject {

    public OBJ_Trash(GamePanel gp) {
        super(Type.TRASH);
        this.name = "Trash";
        this.collision = false;

        randomizeType();
    }

    private void randomizeType() {
        Random rand = new Random();
        int r = rand.nextInt(3);

        try {
            switch (r) {
                case 0:
                    this.displayName = "Botol Cola";
                    this.description = "Sampah botol plastik.";
                    this.category = "Anorganic";
                    this.binType = TrashType.ANORGANIC;
                    this.image = ImageIO
                            .read(getClass().getResourceAsStream("/objects/trash/trash_anorganic_botol_cola.png"));
                    break;
                case 1:
                    this.displayName = "Pisang";
                    this.description = "Kulit pisang sisa.";
                    this.category = "Organic";
                    this.binType = TrashType.ORGANIC;
                    this.image = ImageIO
                            .read(getClass().getResourceAsStream("/objects/trash/trash_organic_pisang.png"));
                    break;
                case 2:
                    this.displayName = "Baterai";
                    this.description = "Baterai bekas.";
                    this.category = "B3";
                    this.binType = TrashType.TOXIC;
                    this.image = ImageIO.read(getClass().getResourceAsStream("/objects/trash/trash_b3_baterai.png"));
                    break;
            }
        } catch (Exception e) {
            System.out.println("[OBJ_Trash] Failed to load trash image");
            e.printStackTrace();
        }
    }
}
