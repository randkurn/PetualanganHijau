package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.AudioManager;
import model.GameStateManager;

public class MenuController {

    @FXML
    private Button btnNewGame;

    @FXML
    private Button btnLoadGame;

    @FXML
    public void initialize() {
        AudioManager.playMenuMusic();
        updateLoadButtonState();
    }

    private void updateLoadButtonState() {
        boolean hasSave = GameStateManager.hasAnySave();
        btnLoadGame.setDisable(!hasSave);
    }

    @FXML
    public void startNewGame() {
        NewGameDialogController.NewGameRequest request = openNewGameDialog();
        if (request == null) {
            return;
        }
        try {
            GameStateManager.prepareNewGame(request.slot(), request.name());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/prolog.fxml"));
            Parent prologRoot = loader.load();

            Scene scene = btnNewGame.getScene();
            scene.setRoot(prologRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadGame() {
        if (btnLoadGame.isDisabled()) {
            return;
        }
        Integer slot = openLoadDialog();
        if (slot == null) {
            return;
        }

        if (!GameStateManager.prepareLoad(slot)) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Load Game");
            alert.setHeaderText("Gagal membuka slot");
            alert.setContentText("Data mungkin korup atau telah dihapus.");
            alert.showAndWait();
            updateLoadButtonState();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
            Parent gameRoot = loader.load();

            Scene scene = btnLoadGame.getScene();
            scene.setRoot(gameRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openOptions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/options.fxml"));
            Parent dialogRoot = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Options - Audio");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(btnNewGame.getScene().getWindow());
            dialog.setScene(new Scene(dialogRoot));
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exitGame() {
        System.exit(0);
    }

    private NewGameDialogController.NewGameRequest openNewGameDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/new_game_dialog.fxml"));
            Parent dialogRoot = loader.load();
            NewGameDialogController controller = loader.getController();
            controller.setSlotInfos(GameStateManager.getAllSlotInfo());

            Stage dialog = new Stage();
            controller.setStage(dialog);
            dialog.setTitle("New Game");
            dialog.initOwner(btnNewGame.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(dialogRoot));
            dialog.showAndWait();
            return controller.getResult().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Integer openLoadDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/load_game_dialog.fxml"));
            Parent dialogRoot = loader.load();
            LoadGameDialogController controller = loader.getController();
            controller.setSlotInfos(GameStateManager.getAllSlotInfo());

            Stage dialog = new Stage();
            controller.setStage(dialog);
            dialog.setTitle("Load Game");
            dialog.initOwner(btnLoadGame.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(dialogRoot));
            dialog.showAndWait();
            return controller.getSelectedSlot().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
