package model;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.InputStream;

public class SpriteSheet {

    private final Image sheet;
    private final int tileW, tileH;

    public SpriteSheet(InputStream in, int tileW, int tileH) {
        this.sheet = new Image(in);
        this.tileW = tileW;
        this.tileH = tileH;
    }

    public Image getFrame(int row, int col) {
        return new WritableImage(
                sheet.getPixelReader(),
                col * tileW,
                row * tileH,
                tileW, tileH
        );
    }
}
