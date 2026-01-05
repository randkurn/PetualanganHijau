package model;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import controller.GamePanel;

public class OBJ_Tree extends GameObject {

    public enum TreeStage {
        SEED,
        SAPLING,
        GROWING,
        MATURE
    }

    private TreeStage stage;
    private int plantingDay;
    private static final int DAYS_TO_MATURE = 7;

    private BufferedImage seedImage;
    private BufferedImage saplingImage;
    private BufferedImage growingImage;
    private BufferedImage matureImage;

    public OBJ_Tree() {
        this(-1);
    }

    public OBJ_Tree(int plantingDay) {
        super(Type.DECORATION);
        this.displayName = "Pohon";
        this.collision = true;
        this.pickable = false;
        this.stage = TreeStage.SEED;
        this.plantingDay = plantingDay;

        loadImages();
        updateImage();
    }

    private void loadImages() {
        try {
            seedImage = ImageIO.read(getClass().getResourceAsStream("/objects/tree1.png"));
            saplingImage = ImageIO.read(getClass().getResourceAsStream("/objects/tree2.png"));
            growingImage = ImageIO.read(getClass().getResourceAsStream("/objects/tree3.png"));
            matureImage = ImageIO.read(getClass().getResourceAsStream("/objects/tree4.png"));

            if (seedImage == null || saplingImage == null || growingImage == null || matureImage == null) {
                System.err.println("Failed to load one or more tree sprites!");
            }

        } catch (Exception e) {
            System.err.println("Error loading tree sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateImage() {
        switch (stage) {
            case SEED -> image = seedImage;
            case SAPLING -> image = saplingImage;
            case GROWING -> image = growingImage;
            case MATURE -> image = matureImage;
        }
    }

    @Override
    public void update(GamePanel gp) {
        super.update(gp);

        // If plantingDay is not set, initialize it to current day.
        // Note: For existing trees on map load without saved data,
        if (plantingDay == -1) {
            plantingDay = gp.timeM.getCurrentDay();
        }

        int currentDay = gp.timeM.getCurrentDay();
        int daysPassed = currentDay - plantingDay;

        TreeStage oldStage = stage;

        if (daysPassed >= DAYS_TO_MATURE) {
            stage = TreeStage.MATURE;
        } else if (daysPassed >= 4) {
            stage = TreeStage.GROWING;
        } else if (daysPassed >= 2) {
            stage = TreeStage.SAPLING;
        } else {
            stage = TreeStage.SEED;
        }

        if (oldStage != stage) {
            updateImage();
        }
    }

    @Override
    public void interact(GamePanel gp) {
        String status = switch (stage) {
            case SEED -> "biji yang baru ditanam";
            case SAPLING -> "bibit kecil";
            case GROWING -> "sedang tumbuh";
            case MATURE -> "pohon dewasa yang sehat";
        };
        gp.uiM.getPlayScreen().showDialog("Ini adalah " + status + ".", "Pohon");
    }
}
