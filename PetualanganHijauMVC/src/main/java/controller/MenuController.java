package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.DataManager;

public class MenuController {

    @FXML
    private Button btnNewGame;

    @FXML
    private Button btnLoadGame;

    private static final String DEFAULT_SAVE_FILE = "player.csv";

    @FXML
    public void initialize() {
        updateLoadButtonState();
    }

    private void updateLoadButtonState() {
        boolean hasSave = DataManager.hasSaveData(DEFAULT_SAVE_FILE);
        btnLoadGame.setDisable(!hasSave);
    }

    @FXML
    public void startNewGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/prolog.fxml"));
            Scene prologScene = new Scene(loader.load());

            Stage stage = (Stage) btnNewGame.getScene().getWindow();
            stage.setScene(prologScene);
            stage.setTitle("Petualangan Hijau");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadGame() {
        if (btnLoadGame.isDisabled()) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
            Scene gameScene = new Scene(loader.load());

            Stage stage = (Stage) btnLoadGame.getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("Petualangan Hijau");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openOptions() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Pengaturan");
        alert.setHeaderText("Belum tersedia");
        alert.setContentText("Menu pengaturan akan hadir pada versi berikutnya.");
        alert.showAndWait();
    }

    @FXML
    public void exitGame() {
        System.exit(0);
    }
}
