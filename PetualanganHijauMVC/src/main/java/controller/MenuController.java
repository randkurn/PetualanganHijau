package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Button btnStart;

    @FXML
    public void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
            Parent gameRoot = loader.load();

            Stage stage = (Stage) btnStart.getScene().getWindow();
            stage.setScene(new Scene(gameRoot));
            stage.setTitle("Petualangan Hijau");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exitGame() {
        System.exit(0);
    }
}
